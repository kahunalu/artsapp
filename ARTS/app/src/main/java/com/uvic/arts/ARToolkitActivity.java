package com.uvic.arts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

public class ARToolkitActivity extends ARActivity{
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artoolkit);

        // Decode image create bitmap
        byte[] decodedString = Base64.decode(getIntent().getStringExtra(ARTSConstants.IMAGE_CONTENT), Base64.DEFAULT);
        this.bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    protected ARRenderer supplyRenderer() {
        return new RenderObject(this.bitmap);
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) this.findViewById(R.id.aRToolkitFrameLayout);
    }
}
