package com.example.dbm0204.caltracker;

import android.content.Context;
import android.graphics.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by dbm0204 on 7/20/17.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraView(Context context, Camera camera) {
        super(context);
        mCamera=camera;
        mHolder=getHolder();
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //The surface has been created, now tell the camera to draw the preview
        if(mCamera!=null){
            try {
                mCamera.setPreviewDisplaygi(holder);
                mCamera.startPreview();
            } catch (IOException e){
                Log.e("Surface Created","Exception",e);

            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
