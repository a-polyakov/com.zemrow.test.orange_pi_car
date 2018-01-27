package com.zemrow.orangepi.orangepi_car;

public class Motor {

    public static final int SPEED_MIN = -100;
    public static final int SPEED_MAX = 100;

    private int speed;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) throws Exception {
        if (speed < SPEED_MIN || speed > SPEED_MAX) {
            throw new IllegalArgumentException("Speed " + speed + " not range " + SPEED_MIN + ".." + SPEED_MAX);
        }
        this.speed = speed;
    }
}
