package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.OrangePiPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CDevice;

public class PiCar extends Car {
    private static final Pin MOTOR_LEFT_PIN_A = OrangePiPin.GPIO_00;
    private static final Pin MOTOR_LEFT_PIN_B = OrangePiPin.GPIO_01;
    private static final int MOTOR_LEFT_I2C_SPEED_CHANNEL = 4;
    private static final Pin MOTOR_RIGHT_PIN_A = OrangePiPin.GPIO_02;
    private static final Pin MOTOR_RIGHT_PIN_B = OrangePiPin.GPIO_03;
    private static final int MOTOR_RIGHT_I2C_SPEED_CHANNEL = 5;

    private static final int HELM_I2C_CHANNEL = 0;
    private static final int CAM_LEFT_RIGHT_I2C_CHANNEL = 14;
    private static final int CAM_TOP_BUTTON_I2C_CHANNEL = 15;


    public PiCar(GpioController gpio, I2CDevice device) {
        super(
                new PiMotor(gpio, device, MOTOR_LEFT_PIN_A, MOTOR_LEFT_PIN_B, MOTOR_LEFT_I2C_SPEED_CHANNEL),
                new PiMotor(gpio, device, MOTOR_RIGHT_PIN_A, MOTOR_RIGHT_PIN_B, MOTOR_RIGHT_I2C_SPEED_CHANNEL),
                new PiServo(device, HELM_I2C_CHANNEL),
                new PiServo(device, CAM_LEFT_RIGHT_I2C_CHANNEL),
                new PiServo(device, CAM_TOP_BUTTON_I2C_CHANNEL)
                );
    }
}
