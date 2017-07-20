package com.example.dbm0204.caltracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by dbm0204 on 7/20/17.
 */

public class SQLQueryHelper {
    public SQLQueryHelper() {}
    private static final String[] ALL_COLUMNS = new String[] {
            SQLHelper.COLUMN_ID, SQLHelper.COLUMN_MEAL_TYPE,
            SQLHelper.COLUMN_TIME, SQLHelper.COLUMN_FOOD_LIST,
            SQLHelper.COLUMN_NEW, SQLHelper.COLUMN_CHANGED,
            SQLHelper.COLUMN_DELETED, SQLHelper.COLUMN_SERVER_ID};

    public static long insertMeal(Meal meal){
        SQLiteDatabase db = SQLHelper.getInstance().getWritableDatabase();
        meal.setId(db.insertWithOnConflict(SQLHelper.TABLE_MEALS,null,meal.toContentValues(),SQLiteDatabase.CONFLICT_REPLACE));
        return meal.getId();
    }
    public static int insertMeals(List<Meal> meals){
        ContentValues[] cvs = new ContentValues[meals.size()];
        for (int i=0;i<meals.size();i++){
            cvs[i]=meals.get(i).toContentValues();
        }
        return SQLHelper.bulkInsert(SQLHelper.TABLE_MEALS,cvs);
    }
    public static void updateMeals(Meal meal){
        SQLiteDatabase db = SQLHelper.getInstance().getWritableDatabase();
        db.update(SQLHelper.TABLE_MEALS,meal.toContentValues(),SQLHelper.COLUMN_ID+"="+meal.getId(),null);

    }
    public static Meal getMeal(long mealId) {
        SQLiteDatabase db = SQLHelper.getInstance().getReadableDatabase();

        Cursor c = db.query(SQLHelper.TABLE_MEALS, ALL_COLUMNS,
                SQLHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(mealId)},
                null, null, null);

        if(c != null && c.moveToFirst()) {
            Meal meal = new Meal(c.getLong(0), c.getLong(2), c.getString(1), (ArrayList<FoodItem>) bytesToFoodList(c.getBlob(3)), c.getInt(4) == 1, c.getInt(5), c.getInt(6) == 1, c.getLong(7));

            c.close();

            return meal;
        } else if(c != null) {
            c.close();
        }

        return null;
    }
    public static List<Meal> getMeals(Calendar startDay, Calendar endDay, boolean useFullDay) {
        if(useFullDay) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(startDay.getTimeInMillis());
            startDay = cal;
            DateUtils.toStartOfDay(startDay);

            cal = new GregorianCalendar();
            cal.setTimeInMillis(endDay.getTimeInMillis());
            endDay = cal;
            DateUtils.toEndOfDay(endDay);
        }

        List<Meal> mealList = new ArrayList<>();
        SQLiteDatabase db = SQLHelper.getInstance().getReadableDatabase();

        Cursor c = db.query(SQLHelper.TABLE_MEALS, ALL_COLUMNS,
                SQLHelper.COLUMN_TIME + " >= " + startDay.getTimeInMillis() + " AND " + SQLHelper.COLUMN_TIME + " <= " + endDay.getTimeInMillis() + " AND " + SQLHelper.COLUMN_DELETED + "!= 1",
                null, null, null, SQLHelper.COLUMN_TIME);

        if(c != null && c.moveToFirst()) {
            while(!c.isAfterLast()) {
                mealList.add(new Meal(c.getLong(0), c.getLong(2), c.getString(1), (ArrayList<FoodItem>) bytesToFoodList(c.getBlob(3)), c.getInt(4) == 1, c.getInt(5), c.getInt(6) == 1, c.getLong(7)));
                c.moveToNext();
            }
            c.close();

            return mealList;
        } else if(c != null) {
            c.close();
            return mealList;
        }

        return mealList;
    }
    public static List<Meal> getMealsBefore(Calendar endDay, boolean useFullDay) {
        if(useFullDay) {
            endDay.add(Calendar.DATE, 1);
            endDay.set(Calendar.HOUR_OF_DAY, 0);
            endDay.set(Calendar.MINUTE, 0);
            endDay.set(Calendar.SECOND, 0);
            endDay.set(Calendar.MILLISECOND, 0);
        }

        List<Meal> mealList = new ArrayList<>();
        SQLiteDatabase db = SQLHelper.getInstance().getReadableDatabase();

        Cursor c = db.query(SQLHelper.TABLE_MEALS, ALL_COLUMNS,
                SQLHelper.COLUMN_TIME + " <= " + endDay.getTimeInMillis(),
                null, null, null, SQLHelper.COLUMN_TIME);

        if(c != null && c.moveToFirst()) {
            while(!c.isAfterLast()) {
                mealList.add(new Meal(c.getLong(0), c.getLong(2), c.getString(1), (ArrayList<FoodItem>) bytesToFoodList(c.getBlob(3)), c.getInt(4) == 1, c.getInt(5), c.getInt(6) == 1, c.getLong(7)));
                c.moveToNext();
            }
            c.close();

            return mealList;
        } else if(c != null) {
            c.close();
            return mealList;
        }

        return mealList;
    }
    public static List<Meal> getChangedMeals() {
        List<Meal> mealList = new ArrayList<>();
        SQLiteDatabase db = SQLHelper.getInstance().getReadableDatabase();

        Cursor c = db.query(SQLHelper.TABLE_MEALS, ALL_COLUMNS, SQLHelper.COLUMN_CHANGED + " > 0", null, null, null, SQLHelper.COLUMN_TIME);

        if(c != null && c.moveToFirst()) {
            while(!c.isAfterLast()) {
                mealList.add(new Meal(c.getLong(0), c.getLong(2), c.getString(1), (ArrayList<FoodItem>) bytesToFoodList(c.getBlob(3)), c.getInt(4) == 1, c.getInt(5), c.getInt(6) == 1, c.getLong(7)));
                c.moveToNext();
            }
            c.close();

            return mealList;
        } else if(c != null) {
            c.close();
            return mealList;
        }

        return mealList;
    }
    public static List<Meal> getMeals() {
        List<Meal> mealList = new ArrayList<>();
        SQLiteDatabase db = SQLHelper.getInstance().getReadableDatabase();

        Cursor c = db.query(SQLHelper.TABLE_MEALS, ALL_COLUMNS, null, null, null, null, SQLHelper.COLUMN_TIME);

        if(c != null && c.moveToFirst()) {
            while(!c.isAfterLast()) {
                mealList.add(new Meal(c.getLong(0), c.getLong(2), c.getString(1), (ArrayList<FoodItem>) bytesToFoodList(c.getBlob(3)), c.getInt(4) == 1, c.getInt(5), c.getInt(6) == 1, c.getLong(7)));
                c.moveToNext();
            }
            c.close();

            return mealList;
        } else if(c != null) {
            c.close();
            return mealList;
        }

        return mealList;
    }
    public static void deleteMeal(Meal meal) {
        SQLHelper.getInstance().getWritableDatabase().delete(SQLHelper.TABLE_MEALS, SQLHelper.COLUMN_ID + " = " + meal.getId(), null);
    }


    /**
     * Delete several meals from the database at once
     *
     * @param meals	<code>List</code> of meals to be deleted.
     */
    public static int deleteMeals(List<Meal> meals) {
        int deleted = 0;
        SQLiteDatabase mDb = SQLHelper.getInstance().getWritableDatabase();

        mDb.beginTransaction();
        try {
            for (Meal meal : meals) {
                mDb.delete(SQLHelper.TABLE_MEALS, SQLHelper.COLUMN_ID + " = " + meal.getId(), null);
                deleted++;
            }
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }

        return deleted;
    }


    public static byte[] foodListToBytes(List<FoodItem> foodItems) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        try (ObjectOutputStream objectOut = new ObjectOutputStream(byteOut)) {
            objectOut.writeObject(foodItems);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    public static List<FoodItem> bytesToFoodList(byte[] bytes) {
        ArrayList<FoodItem> foodItems;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);

        try (ObjectInputStream objectIn = new ObjectInputStream(byteIn)) {
            //noinspection unchecked
            foodItems = (ArrayList<FoodItem>) objectIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }

        return foodItems;
    }
}
