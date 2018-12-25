package com.game.rk6cooperation.androidgame.Game;

import com.game.rk6cooperation.androidgame.GameActivity;

public class RunningNumber {
    private int indexOfRow;
    private String valueInString;

    private double xCoord;
    private double velocity;

    public RunningNumber(String _value, int _indexOfRow, double _velocity) {
        valueInString = _value;
        indexOfRow = _indexOfRow;
        velocity = _velocity;
        xCoord = 0;
    }

    public void move(double dx) {
        xCoord += dx;
    }

    public int getIndexOfRow() {
        return indexOfRow;
    }

    public void setIndexOfRow(int indexOfRow) {
        this.indexOfRow = indexOfRow;
    }

    public String getValueInString() {
        return valueInString;
    }

    public void setValueInString(String valueInString) {
        this.valueInString = valueInString;
    }

    public double getxCoord() {
        return xCoord;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

}
