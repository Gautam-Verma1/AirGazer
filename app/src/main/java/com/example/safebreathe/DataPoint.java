package com.example.safebreathe;

public class DataPoint {
    int xValue, yValue;

    public DataPoint(int xValue, int yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public DataPoint() {

    }

    public int getyValue() {
        return yValue;
    }

    public int getxValue() {
        return xValue;
    }
}
