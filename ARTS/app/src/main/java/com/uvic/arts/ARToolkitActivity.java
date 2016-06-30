package com.uvic.arts;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.FrameLayout;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

public class ARToolkitActivity extends ARActivity{
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artoolkit);

        this.bitmap = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
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
