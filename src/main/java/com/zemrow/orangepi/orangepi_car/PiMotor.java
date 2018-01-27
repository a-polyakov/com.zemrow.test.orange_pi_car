package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.i2c.I2CDevice;

public class PiMotor extends Motor {
    private final I2CDevice device;
    private final GpioPinDigitalOutput pinA;
    private final GpioPinDigitalOutput pinB;
    private final int i2cSpeedChannel;

    public PiMotor(GpioController gpio, I2CDevice device, Pin pinA, Pin pinB, int i2cSpeedChannel) {
        this.device = device;
        this.pinA = gpio.provisionDigitalOutputPin(pinA, PinState.LOW);
        this.pinB = gpio.provisionDigitalOutputPin(pinB, PinState.LOW);
        this.i2cSpeedChannel = i2cSpeedChannel;
    }

    @Override
    public void setSpeed(int speed) throws Exception {
        if (getSpeed() != speed) {
            super.setSpeed(speed);
            if (speed == 0) {
                pinA.low();
                pinB.low();
            } else {
                PCA9685Helper.write(device, i2cSpeedChannel, 0, PCA9685Helper.MAX_VALUE * Math.abs(speed) / SPEED_MAX);
                if (speed > 0) {
                    pinA.low();
                    pinB.high();
                } else {
                    pinB.low();
                    pinA.high();
                }
            }
        }
    }
}
