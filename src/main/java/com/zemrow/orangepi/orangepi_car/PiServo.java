package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.i2c.I2CDevice;

public class PiServo extends Servo {

    private final I2CDevice device;
    private final int i2cValueChannel;

    public PiServo(I2CDevice device, int i2cValueChannel) {
        this.device = device;
        this.i2cValueChannel = i2cValueChannel;
    }

    @Override
    public void setValue(int value) throws Exception {
        if (value < PCA9685Helper.MIN_VALUE || value > PCA9685Helper.MAX_VALUE) {
            throw new IllegalArgumentException("Value "+value+" not range " + PCA9685Helper.MIN_VALUE + ".." + PCA9685Helper.MAX_VALUE);
        }
        if (getValue() != value) {
            super.setValue(value);
            PCA9685Helper.write(device, i2cValueChannel, 0, value);
        }
    }
}
