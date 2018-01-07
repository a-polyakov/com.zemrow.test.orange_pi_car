package com.zemrow.orangepi.orangepi_car;

public class Motor {
    private int speed;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) throws Exception {
        if (speed < -100 || speed > 100) {
            throw new IllegalArgumentException("Speed "+speed+" not range -100..100");
        }
        this.speed = speed;
    }
}
