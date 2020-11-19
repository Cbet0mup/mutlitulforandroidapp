package com.pavel.multitool.flashLightSupplement;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

public class FlashClass {
    private boolean flashStatus = false;
    private Context context;

    public FlashClass(Context context) {
        this.context = context;
    }

    public void flashOn(){
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE) ;
        try {
            assert cameraManager != null;
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
                flashStatus = true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void flashOff(){
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE) ;
        try {
            assert cameraManager != null;
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
                flashStatus = false;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isFlashStatus() {
        return flashStatus;
    }
}
