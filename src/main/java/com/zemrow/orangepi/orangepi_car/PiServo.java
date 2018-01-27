package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.i2c.I2CDevice;

public class PiServo extends Servo {
    public static final int SG90_MIN_TIME = 700; //TODO
    public static final int SG90_MAX_TIME = 2800; // TODO
    public static final int SG90_ANGLE = 180;

    private final I2CDevice device;
    private final int i2cValueChannel;

    private boolean changeConfig = false;
    /**
     * Продолжительность импульса соответствующее крайнему положению (1/1_000_000 секунд)
     */
    private int min = SG90_MIN_TIME;
    /**
     * Продолжительность импульса соответствующее крайнему положению (1/1_000_000 секунд)
     */
    private int max = SG90_MAX_TIME;

    public PiServo(I2CDevice device, int i2cValueChannel, String name, int minAngle, int maxAngle) {
        super(name, minAngle, maxAngle);
        this.device = device;
        this.i2cValueChannel = i2cValueChannel;
    }

    public void config(int min, int max) {
        this.min = min;
        this.max = max;
        changeConfig = true;
    }

    @Override
    public boolean setAngle(int angle) throws Exception {
        boolean result = super.setAngle(angle);
        if (changeConfig) {
            result = true;
            changeConfig = false;
        }
        if (result) {
            int value = (min + (max - min) * (SG90_ANGLE / 2 + angle) / SG90_ANGLE) // Продолжительность импульса соответствующему углу (-90->min, 90->max)
                    * PCA9685Helper.MAX_VALUE // приводим к маштабу PCA9685
                    * Context.FREQ / 1_000_000; // длительность такта шим
            setValue(value);
        }
        return result;
    }

    private void setValue(int value) throws Exception {
        PCA9685Helper.write(device, i2cValueChannel, 0, value);
    }
}
