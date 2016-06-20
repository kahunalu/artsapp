package com.uvic.arts;


import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.Cube;

import javax.microedition.khronos.opengles.GL10;


public class RenderObject extends ARRenderer {

    private int markerID = -1;
    private Cube cube = new Cube(40.0f, 0.0f, 0.0f, 20.0f);

    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {

        markerID = ARToolKit.getInstance().addMarker("single;Data/hiro.patt;80");
        if (markerID < 0) return false;

        return true;
    }

    /**
     * Override the draw function from ARRenderer.
     */
    @Override
    public void draw(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Apply the ARToolKit projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CW);

        // If the marker is visible, apply its transformation, and draw a cube
        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
            cube.draw(gl);
        }

    }
}
