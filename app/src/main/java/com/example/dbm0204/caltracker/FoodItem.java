package com.example.dbm0204.caltracker;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dbm0204 on 7/20/17.
 */

public class FoodItem extends Nutritious implements Serializable {

    private Map<String, Double> fields;  //holds all nutrition info
    private Map<String, Double> calculatedFields;  //holds all calculated nutrition info

    public static final String KEY_CAL = "Calories";

    private String name;
    private String brand;
    private double servingSize;
    private String servingSizeUnit;  //e.g. cup, grams, taco
    private String actualServingSizeUnit; //e.g. convert "grams of flakes" to "grams"
    private boolean usesVol;
    private boolean usesMass;
    private boolean needConvertVol;   //whether or not the volume needs to be converted
    private boolean needCalculateServings;  //where or not the servings need to be calcualted
    private Set<String> volUnits;
    private Set<String> massUnits;
    private int maxMassLen;
    private int maxVolLen;
    private Density density;
    private double numServings;  //number of servings user has entered, after calculation.

    private double volume;
    private double cubicVolume;  //volume that will always be in inches cubed
    private double mass;

    private static Map<String, Double> densities = null;  //not associated with a single FoodItem

    public FoodItem() {
        //LinkedHashMap used to ensure insertion order is maintained, for iteration.
        fields = new LinkedHashMap<>(7);

        //NOTE: when checking if the serving size matches, call toLower() on it first, and remove
        //'s' at the end of the word, if necessary.

        //Create list of volume units, and find max length
        String[] vol_values = new String[]{"cup", "quart", "pt", "pint", "ml", "milliliter",
                "millilitre", "l", "liter", "litre", "tsp", "teaspoon", "tbsp", "tbl",
                "tablespoon", "fl oz", "fl. oz", "fluid ounce"};
        maxVolLen = Integer.MIN_VALUE;
        for(String s : vol_values) {
            if(s.length() > maxVolLen) {
                maxVolLen = s.length();
            }
        }
        volUnits = new HashSet<>(Arrays.asList(vol_values));

        //Create list of mass units, and find max length
        String[] mass_values = new String[]{"oz", "ounce",  "g", "gram", "mg", "milligram", "lb",
                "pound"};
        maxMassLen = Integer.MIN_VALUE;
        for(String s : mass_values) {
            if(s.length() > maxMassLen) {
                maxMassLen = s.length();
            }
        }
        massUnits = new HashSet<>(Arrays.asList(mass_values));

        density = new Density();
        density.value = 0.0;
        numServings = 0.0;
        volume = 0.0;
        mass = 0.0;
    }

    public void setField(String field, Double value) {
        fields.put(field, value);
    }

    public Double getField(String field) {
        return fields.get(field);
    }

    /**
     * @return set to be iterated through in an enhanced for loop
     */
    public Set<Map.Entry<String, Double>> getSet() {
        return fields.entrySet();
    }

    public Map<String, Double> getFields() { return fields; }

    //region getters and setters for hard-coded fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getServingSize() {
        return servingSize;
    }

    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }

    public String getServingSizeUnit() {
        return servingSizeUnit;
    }

    public void setServingSizeUnit(String servingSizeUnit) {
        //remove last '.' from unit, if necessary
        String formattedUnit = servingSizeUnit;
        if(formattedUnit.charAt(formattedUnit.length() - 1) == '.') {
            formattedUnit = formattedUnit.substring(0, formattedUnit.length() - 1);
        }

        this.servingSizeUnit = formattedUnit;

        //check if volume, mass, or neither is needed

        formattedUnit = formattedUnit.toLowerCase();
        if(formattedUnit.charAt(formattedUnit.length() - 1) == 's') {
            //remove last 's' for comparison
            formattedUnit = formattedUnit.substring(0, formattedUnit.length() - 1);
        }

        if(massUnits.contains(formattedUnit)) {
            usesMass = true;
            usesVol = false;
            needConvertVol = true;
            needCalculateServings = true;
            actualServingSizeUnit = formattedUnit;
        } else if(volUnits.contains(formattedUnit)) {
            usesMass = false;
            usesVol = true;
            needConvertVol = true;
            needCalculateServings = true;
            actualServingSizeUnit = formattedUnit;
        } else {
            //check that each word is not in either, e.g. 'cups (chopped)'
            boolean stopCheck = false;
            String[] unitWords = formattedUnit.split("\\s+");

            //check that words are not in massUnits
            for(String word : unitWords) {
                if(massUnits.contains(word)) {
                    stopCheck = true;
                    usesMass = true;
                    usesVol = false;
                    needConvertVol = true;
                    needCalculateServings = true;
                    actualServingSizeUnit = word;
                    break;
                }
            }
            if(stopCheck) {
                return;
            }

            //check that words are not in volUnits
            for(String word: unitWords) {
                if(volUnits.contains(word)) {
                    stopCheck = true;
                    usesMass = false;
                    usesVol = true;
                    needConvertVol = true;
                    needCalculateServings = true;
                    actualServingSizeUnit = word;
                    break;
                }
            }
            if(stopCheck) {
                return;
            }

            usesMass = false;
            usesVol = false;
            needConvertVol = false;
            needCalculateServings = false;
        }
    }

    public String getActualServingSizeUnit() {
        return actualServingSizeUnit;
    }

    public void setActualServingSizeUnit(String actualServingSizeUnit) {
        this.actualServingSizeUnit = actualServingSizeUnit;
    }

    public boolean usesMass() { return usesMass; }

    public void setUsesMass(boolean value) { this.usesMass = value; }

    public boolean usesVolume() {
        return usesVol;
    }

    public void setUsesVol(boolean value) { this.usesVol = value; }

    public double getNumServings() {
        if ((usesMass || usesVol) && needCalculateServings) {
            if (!calculateNumServings()) {
                // Failed; not enough info yet
                return 0.0;
            }
        }
        return numServings;
    }

    public void setNumServings(double numServings) {
        this.numServings = numServings;
        if (usesVol || usesMass) {
            // Servings were overridden, clear fields.
            needCalculateServings = false;
            needConvertVol = false;
            volume = 0.0;
            cubicVolume = 0.0;
            mass = 0.0;
        }
        calculateNutrition();
    }

    public boolean isNeedCalculateServings() {
        return needCalculateServings;
    }

    public void setNeedCalculateServings(boolean needCalculateServings) {
        this.needCalculateServings = needCalculateServings;
    }

    public boolean needConvertVol() {
        return needConvertVol;
    }

    public void setNeedConvertVol(boolean needConvertVol) {
        this.needConvertVol = needConvertVol;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
        cubicVolume = volume;
        needConvertVol = true;
        needCalculateServings = true;
        calculateNutrition();
    }

    public void setConvertedVolume(double volume) {
        this.volume = volume;
    }

    public double getCubicVolume() {
        return cubicVolume;
    }

    public void setCubicVolume(double cubicVolume) {
        this.cubicVolume = cubicVolume;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    //endregion

    @Override
    public String toString() {
        return name + " (" + brand + ")";
    }

    @Override
    public boolean equals(Object o) {
        if(getClass() != o.getClass()) {
            return false;
        }
        FoodItem fi = (FoodItem) o;
        if(fi.getName().equals(this.getName()) &&
                fi.getBrand().equals(this.getBrand()) &&
                fi.getServingSizeUnit().equals(this.getServingSizeUnit())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Double> getNutrition() {
        return calculateNutrition();
    }

    /**
     * Calculates number of servings
     * @return true is success, false if failure
     */
    public boolean calculateNumServings() {
        if (!needCalculateServings) {
            // already done
            return true;
        }

        if (usesMass) {
            if (volume == 0.0 || density.value == 0.0) {
                return false;
            }

            if (needConvertVol) {
                FoodItem convertedFood = Units.convertVolume(this);
                needConvertVol = false;
                volume = convertedFood.getVolume();
            }

            mass = volume * density.value;

            FoodItem convertedFood = Units.convertMass(this);
            mass = convertedFood.getMass();

            numServings = mass / servingSize;
            needCalculateServings = false;
            return true;

        } else if (usesVol) {
            if (volume == 0.0) {
                return false;
            }

            if (needConvertVol) {
                FoodItem convertedFood = Units.convertVolume(this);
                needConvertVol = false;
                volume = convertedFood.getVolume();
            }

            numServings = volume / servingSize;
            needCalculateServings = false;
            return true;
        }

        // no need to calculate number of servings, user should enter manually
        // (this should never happen anymore)
        needCalculateServings = false;
        return true;
    }

    private class Density implements Serializable {
        private double value;
        private String name;
        private String id;
    }

    public double getDensity() {
        return density.value;
    }

    public void setDensity(double value) {
        this.density.value = value;
    }

    public String getDensityName() {
        return density.name;
    }

    public void setDensityName(String name) {
        density.name = name;
    }

    public String getDensityId() {
        return density.id;
    }

    public void setDensityId(String id) {
        density.id = id;
    }


    // Methods for saving & retrieving densities from database (may be moved)

    public static void addDensity(String name, Double value) {
        if (densities == null) {
            densities = new HashMap<>();
        }
        densities.put(name, value);
    }

    public static String[] getDensityKeys() {
        if (densities == null) {
            return null;
        } else {
            Set<String> densitySet = densities.keySet();
            String[] retArray = densitySet.toArray(new String[densitySet.size()]);
            return retArray;
        }
    }

    public static Double getDensityValue(String name) {
        if (densities == null) {
            return null;
        } else {
            return densities.get(name);
        }
    }

    public static Map<String, Double> getAllDensities() {
        return densities;
    }

    public static void clearAllDensities() {
        densities = new HashMap<>();
    }


//    /**
//     * Gets partial matches from the density database
//     * @param name of food item
//     * @return Map containing all matches
//     */
//    public static Map<String, Double> getDensityPartialMatches(String name) {
//        Map<String, Double> results = new HashMap<>();
//
//        for (Map.Entry<String, Double> entry : densities.entrySet()) {
//            String[] words = name.split("\\s+");
//            for (String word : words)
//            {
//                //boolean contains = entry.getKey().toLowerCase().matches(".*\\b" + word.toLowerCase() + "\\b.*");
//                String cleanedWord = word.replaceAll("[^\\w]","");
//                if (cleanedWord.equals("")) {
//                    continue;
//                }
//                boolean contains = entry.getKey().toLowerCase().matches(".*\\b" + cleanedWord.toLowerCase() + "\\b.*");
//                if(contains) {
//                    results.put(entry.getKey(), entry.getValue());
//                }
//            }
//        }
//
//        return results;
//    }

    /**
     Calculates & saves nutrition info, based on entered/scanned servings.
     @return copy of calculated fields
     */
    public Map<String, Double> calculateNutrition() {
        double numServings = getNumServings();
        Map<String, Double> calculatedFields = new LinkedHashMap<>(7);

        for (Map.Entry<String, Double> field : getSet()) {
            calculatedFields.put(field.getKey(), field.getValue() * numServings);
        }

        this.calculatedFields = new LinkedHashMap<>(calculatedFields);
        return calculatedFields;
    }

}
