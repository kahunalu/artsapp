package com.uvic.arts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

public class ARToolkitActivity extends ARActivity{
    private Bitmap bitmap;
    private int size;
    private boolean firstUpdate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artoolkit);

        String[] parameters = getIntent().getStringArrayExtra(ARTSConstants.CONTENT_DATA);

        // Decode image create bitmap
        byte[] decodedString = Base64.decode(parameters[0], Base64.DEFAULT);
        this.bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        this.size = Integer.parseInt(parameters[1]);
    }

    @Override
    protected ARRenderer supplyRenderer() {
        return new RenderObject(this.bitmap, this.size);
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) this.findViewById(R.id.aRToolkitFrameLayout);
    }

    // Override to remove toast being displayed when camera opens
    @Override
    public void cameraPreviewStarted(int width, int height, int rate, int cameraIndex, boolean cameraIsFrontFacing) {
        if(ARToolKit.getInstance().initialiseAR(width, height, "Data/camera_para.dat", cameraIndex, cameraIsFrontFacing)) {
            Log.i("ARActivity", "Camera initialised");
        } else {
            Log.e("ARActivity", "Error initialising camera. Cannot continue.");
            this.finish();
        }

        this.firstUpdate = true;    }

    // Override to handle the first update to the camera, since we're not able to set firstUpdate from
    // outside of ARActivity
    @Override
    public void cameraPreviewFrame(byte[] frame) {
        if(this.firstUpdate) {
            if(this.renderer.configureARScene()) {
                Log.i("ARActivity", "Scene configured successfully");
            } else {
                Log.e("ARActivity", "Error configuring scene. Cannot continue.");
                this.finish();
            }

            this.firstUpdate = false;
        }

        super.cameraPreviewFrame(frame);
    }

    // Try and avoid having ARActivity open when pressing back
    @Override
    public void onBackPressed() {
        super.finish();
        this.finish();
    }
}
