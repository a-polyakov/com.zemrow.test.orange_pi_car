package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

public class PCA9685Helper {

    public static final int I2C_ADDRESS = 0x40;

    private static final int I2C_MODE1 = 0x00;
    private static final int I2C_PRESCALE = 0xFE;

    private static final int _LED0_ON_L = 0x06;
    private static final int _LED0_ON_H = 0x07;
    private static final int _LED0_OFF_L = 0x08;
    private static final int _LED0_OFF_H = 0x09;

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 0xFFF; // 4096: 12-bit

    public static void setPWMFreq(I2CDevice device, int freq) throws IOException, InterruptedException {
        device.write(I2C_MODE1, (byte) 0x00);
        float preScaleVal = 25_000_000.0f; // 25MHz
        preScaleVal /= 4096.0;           // 4096: 12-bit
        preScaleVal /= freq;
        preScaleVal -= 1.0;
        double preScale = Math.floor(preScaleVal + 0.5);
        byte oldmode = (byte) device.read(I2C_MODE1);
        byte newmode = (byte) ((oldmode & 0x7F) | 0x10); // sleep
        device.write(I2C_MODE1, newmode);              // go to sleep
        device.write(I2C_PRESCALE, (byte) (Math.floor(preScale)));
        device.write(I2C_MODE1, oldmode);
        Thread.sleep(5);
        device.write(I2C_MODE1, (byte) (oldmode | 0x80));
    }

    /**
     * @param device
     * @param channel 0..15
     * @param on      0..4095 (2^12 positions)
     * @param off     0..4095 (2^12 positions)
     * @throws IOException
     */
    public static void write(I2CDevice device, int channel, int on, int off) throws IOException {
        device.write(_LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
        device.write(_LED0_ON_H + 4 * channel, (byte) (on >> 8));
        device.write(_LED0_OFF_L + 4 * channel, (byte) (off & 0xFF));
        device.write(_LED0_OFF_H + 4 * channel, (byte) (off >> 8));
    }
}
