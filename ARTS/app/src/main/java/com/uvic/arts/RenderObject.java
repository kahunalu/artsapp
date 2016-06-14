package com.uvic.arts;


import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.Cube;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Sarah on 2016-06-14.
 */
public class RenderObject extends ARRenderer {

    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {
        return true;
    }

    /**
     * Override the draw function from ARRenderer.
     */
    @Override
    public void draw(GL10 gl) {


    }
}
