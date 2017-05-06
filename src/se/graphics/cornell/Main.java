package se.graphics.cornell;

import processing.core.PApplet;

import static se.graphics.cornell.Vector3.*;
import static se.graphics.cornell.Matrix3.*;

public final class Main extends PApplet {
    
    private final static int SIZE = 1600;
    private final static int RESOLUTION = 400;
    
    private Vector3 C = vec3(0f, 0f, -3.1f);
    private Matrix3 R = identityMatrix();
    
    private float drx = 0f;
    private float dry = 0f;
    
    private boolean wKey, aKey, sKey, dKey, shiftKey, spaceKey, leftKey, rightKey, upKey, downKey;
    
    private long totalTime = 0;
    private int count = 0;

    public static void main(String[] args) {
        PApplet.main("se.graphics.cornell.Main");
    }
    
    public void settings() {
        size(SIZE, SIZE);
    }
    
    public void setup() {
        background(0);
        noStroke();
    }
    
    public void draw() {
        background(0);        
        updateCR();
        
        long t = System.currentTimeMillis();        
//        Lab2.drawLowRes(this, SIZE, RESOLUTION, RESOLUTION, C, R);
        Lab2.draw(this, SIZE, SIZE, C, R);
//        Lab3.drawLowRes(this, SIZE, RESOLUTION, RESOLUTION, C, R);
        long dt = System.currentTimeMillis() - t;
        totalTime += dt;
        ++count;
        System.out.println("time: " + dt + "ms, average time: " + (totalTime / count) + "ms"); 
    }
    
    private void updateCR() {
        Vector3 dir = zeros();
//        float smoother = RESOLUTION / 500f;
        float smoother = 1f;
        float ddrx = 0f;
        float ddry = 0f;
        
        if (wKey) {
            dir = vec3(0f, 0f, 0.1f);
        }
        if (aKey) {
            dir = vec3(-0.1f, 0f, 0f);
        }
        if (sKey) {
            dir = vec3(0f, 0f, -0.1f);
        }
        if (dKey) {
            dir = vec3(0.1f, 0f, 0f);
        }
        if (shiftKey) {
            dir = vec3(0f, 0.1f, 0f);
        }
        if (spaceKey) {
            dir = vec3(0f, -0.1f, 0f);
        }
        
        if (leftKey) {
            ddry = 0.1f;
        }
        if (rightKey) {
            ddry = -0.1f;
        }
        if (upKey) {
            ddrx = -0.1f;
        }
        if (downKey) {
            ddrx = 0.1f;
        }
        
        dir = dir.times(smoother);
        drx += ddrx * smoother;
        dry += ddry * smoother;

        R = Matrix3.yRotationMatrix(dry).times(Matrix3.xRotationMatrix(drx));
        C = C.plus(R.times(dir));
    }
        
    public void keyPressed() {
        if (key == 'w') {
            wKey = true;
        }
        if (key == 'a') {
            aKey = true;
        }
        if (key == 's') {
            sKey = true;
        }
        if (key == 'd') {
            dKey = true;
        }
        if (keyCode == SHIFT) {
            shiftKey = true;
        }
        if (key == ' ') {
            spaceKey = true;
        }
        if (keyCode == LEFT) {
            leftKey = true;
        }
        if (keyCode == RIGHT) {
            rightKey = true;
        }
        if (keyCode == UP) {
            upKey = true;
        }
        if (keyCode == DOWN) {
            downKey = true;
        }
        if (keyCode == ENTER) {
            System.out.println(C + " " + drx  + " " + dry);
            noLoop();
        }
    }
    
    public void keyReleased() {
        wKey     = false;
        aKey     = false;
        sKey     = false;
        dKey     = false;
        shiftKey = false;
        spaceKey = false;
        leftKey  = false;
        rightKey = false;
        upKey    = false;
        downKey  = false;
    }
    
    public void mousePressed() {
        String name = System.currentTimeMillis() + ".png";
        System.out.println("saving image as " + name);
        saveFrame(name);
    }
    
}
