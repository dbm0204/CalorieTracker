package com.example.dbm0204.caltracker;

/**
 * Created by dbm0204 on 7/20/17.
 */

public class Line {
    private float x1,x2,y1,y2;

    public Line(float x1, float x2, float y1, float y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    public double getLength(){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    public boolean intersect(){
        return true;
    }
    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public float getY2() {

        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }
}
