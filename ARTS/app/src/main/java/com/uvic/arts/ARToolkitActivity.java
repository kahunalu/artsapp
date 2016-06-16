package com.uvic.arts;

import android.os.Bundle;
import android.widget.FrameLayout;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

public class ARToolkitActivity extends ARActivity{
    private String arContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artoolkit);

        arContent = getIntent().getStringExtra(ARTSConstants.INTENT_CONTENT);
    }

    @Override
    protected ARRenderer supplyRenderer() {
        return new RenderObject();
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) this.findViewById(R.id.aRToolkitFrameLayout);
    }
}
