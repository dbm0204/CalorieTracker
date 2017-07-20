package com.example.dbm0204.caltracker;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import java.io.File;

/**
 * Created by dbm0204 on 7/20/17.
 */

public class ImageDirectoryManager {
    public static final  String IMAGE_DIR_NAME="CameraImages";
    public static final String DENSITY_DIR_NAME="DensityEntries";
    public static final String PPI_DIR_NAME="PixelsPerInch";


    public static boolean clearImageDirectory(Context context){
        boolean allDeleted= true;
        File dir = getImageDirectory(context);
        for (File f: dir.listFiles()){
            if(!f.delete()){
                allDeleted=false;
            }
        }
        return allDeleted;
    }
    public static  File getImageDirectory(Context context){
        File imageDir = new File(context.getCacheDir(), IMAGE_DIR_NAME);
        if(!imageDir.exists()){
            imageDir.mkdir();
        }
        return imageDir;
    }
    public static  File getDensityDirectory(Context context){
        File densityDir = new File(context.getCacheDir(),DENSITY_DIR_NAME);
        if(!densityDir.exists()){
            densityDir.mkdir();
        }
        return densityDir;
    }
    public static File getPixelDirectory(Context context){
        File ppiDir = new File(context.getCacheDir(), PPI_DIR_NAME);
        if(!ppiDir.exists()){
            ppiDir.mkdir();
        }
        return ppiDir;

    }

}
