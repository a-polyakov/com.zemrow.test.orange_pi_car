package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

public class PCA9685Helper {

    public static final int I2C_ADDRESS = 0x40; // адрес устройства

    private static final int I2C_MODE1 = 0x00; // Регистр
    private static final int I2C_MODE1_ALLCALL = 0x01; // bit 0 ALLCALL
    private static final int I2C_MODE1_SUB3 = 0x2; // bit 1 SUB3
    private static final int I2C_MODE1_SUB2 = 0x4; // bit 2 SUB2
    private static final int I2C_MODE1_SUB1 = 0x8; // bit 3 SUB1
    private static final int I2C_MODE1_SLEEP = 0x10; // bit 4 SLEEP
    private static final int I2C_MODE1_AI = 0x20; // bit 5 AI
    private static final int I2C_MODE1_EXTCLK = 0x40; // bit 6 EXTCLK
    private static final int I2C_MODE1_RESTART = 0x80; // bit 7 RESTART
    private static final int I2C_PRESCALE = 0xFE; // Регистр отвечает за частоту ШИМ

    // Регистры на включение
    private static final int _LED0_ON_L = 0x06; // младшие 8 бит
    private static final int _LED0_ON_H = 0x07; // старшие 4 бита
    // Регистры на выключение
    private static final int _LED0_OFF_L = 0x08; // младшие 8 бит
    private static final int _LED0_OFF_H = 0x09; // старшие 4 бита

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 0xFFF; // 0..4095: 4096 кол-во значений содержащихся в 12 битах

    /**
     * @param device
     * @param freq   желаемая частота ШИМ 40..1000
     * @throws IOException
     * @throws InterruptedException
     */
    public static void setPWMFreq(I2CDevice device, int freq) throws IOException, InterruptedException {
        if (freq < 40 || freq > 1000) {
            throw new IllegalArgumentException("Frequency " + freq + " not range 40..1000");
        }
        device.write(I2C_MODE1, (byte) 0x00);
        float preScaleVal = 25_000_000.0f; // внутренний источник тактирования работает на частоте 25MHz
        preScaleVal /= 4096.0;           // 4096: 12-bit
        preScaleVal /= freq;
        preScaleVal -= 1.0;
        double preScale = Math.floor(preScaleVal + 0.5);
        byte oldmode = (byte) device.read(I2C_MODE1);
        byte newmode = (byte) ((oldmode & 0x7F) | I2C_MODE1_SLEEP); // sleep
        device.write(I2C_MODE1, newmode);              // go to sleep
        device.write(I2C_PRESCALE, (byte) (Math.floor(preScale)));
        device.write(I2C_MODE1, oldmode);
        Thread.sleep(5);
        device.write(I2C_MODE1, (byte) (oldmode | I2C_MODE1_RESTART));
    }

    /**
     * @param device
     * @param channel 0..15
     * @param on      0..4095 (2^12 positions)
     * @param off     0..4095 (2^12 positions)
     * @throws IOException
     */
    public static void write(I2CDevice device, int channel, int on, int off) throws IOException {
        if (on < PCA9685Helper.MIN_VALUE || on > PCA9685Helper.MAX_VALUE) {
            throw new IllegalArgumentException("Value on " + on + " not range " + PCA9685Helper.MIN_VALUE + ".." + PCA9685Helper.MAX_VALUE);
        }
        if (off < PCA9685Helper.MIN_VALUE || off > PCA9685Helper.MAX_VALUE) {
            throw new IllegalArgumentException("Value off " + off + " not range " + PCA9685Helper.MIN_VALUE + ".." + PCA9685Helper.MAX_VALUE);
        }
        device.write(_LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
        device.write(_LED0_ON_H + 4 * channel, (byte) (on >> 8));
        device.write(_LED0_OFF_L + 4 * channel, (byte) (off & 0xFF));
        device.write(_LED0_OFF_H + 4 * channel, (byte) (off >> 8));
    }
}
