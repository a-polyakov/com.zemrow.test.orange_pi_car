package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;

import java.io.IOException;

public class Test2 {
    private static final Pin Motor0_A = OrangePiPin.GPIO_00;
    private static final Pin Motor0_B = OrangePiPin.GPIO_01;
    private static final Pin Motor1_A = OrangePiPin.GPIO_02;
    private static final Pin Motor1_B = OrangePiPin.GPIO_03;

    private static final int I2C_MOTOR0_SPEED = 4;
    private static final int I2C_MOTOR1_SPEED = 5;

    private static GpioPinDigitalOutput m0a;
    private static GpioPinDigitalOutput m0b;
    private static GpioPinDigitalOutput m1a;
    private static GpioPinDigitalOutput m1b;

    private static final int I2C_ADDRESS = 0x40;


    private static final int I2C_HELM = 0;
    private static final int I2C_CAM_LEFT_RIGHT = 14;
    private static final int I2C_CAM_TOP_BUTTON = 15;


    public static void main(String[] args) throws PlatformAlreadyAssignedException, InterruptedException, IOException, I2CFactory.UnsupportedBusNumberException {
        PlatformManager.setPlatform(Platform.ORANGEPI);
        GpioController gpio = null;
        try {
            gpio = GpioFactory.getInstance();
            m0a = gpio.provisionDigitalOutputPin(Motor0_A, PinState.LOW);
            m0b = gpio.provisionDigitalOutputPin(Motor0_B, PinState.LOW);
            m1a = gpio.provisionDigitalOutputPin(Motor1_A, PinState.LOW);
            m1b = gpio.provisionDigitalOutputPin(Motor1_B, PinState.LOW);
            I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_0);
            I2CDevice device = i2c.getDevice(I2C_ADDRESS);

            PCA9685Helper.setPWMFreq(device, 60);
            System.out.println("0012");
            PCA9685Helper.write(device, I2C_MOTOR0_SPEED, 0, 4095);
            System.out.println("0013");
            PCA9685Helper.write(device, I2C_MOTOR1_SPEED, 0, 4095);

            System.out.println("forward");
            forward();
            Thread.sleep(500);

            System.out.println("stop");
            stop();
            Thread.sleep(500);

            System.out.println("backward");
            backward();
            Thread.sleep(500);

            System.out.println("stop");
            stop();
            Thread.sleep(500);

            for (int i=200; i<700; i+=25) {
                System.out.println("helm "+i);
                PCA9685Helper.write(device, I2C_HELM, 0, i);
                Thread.sleep(500);
            }
            PCA9685Helper.write(device, I2C_HELM, 0, 300);

//            System.out.println("helm right");
//            write(device, I2C_HELM, 0, 700);
//            Thread.sleep(500);

            for (int i=200; i<700; i+=25) {
                System.out.println("cam "+i);
                PCA9685Helper.write(device, I2C_CAM_LEFT_RIGHT, 0, i);
                Thread.sleep(500);
            }
            PCA9685Helper.write(device, I2C_CAM_LEFT_RIGHT, 0, 440);

//            System.out.println("cam right");
//            write(device, I2C_CAM_LEFT_RIGHT, 0, 700);
//            Thread.sleep(500);

            for (int i=200; i<400; i+=10) {
                System.out.println("cam top "+i);
                PCA9685Helper.write(device, I2C_CAM_TOP_BUTTON, 0, i);
                Thread.sleep(500);
            }
            PCA9685Helper.write(device, I2C_CAM_TOP_BUTTON, 0, 190);

//            System.out.println("cam button");
//            write(device, I2C_CAM_TOP_BUTTON, 0, 700);
//            Thread.sleep(500);

            i2c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (gpio != null) {
                gpio.shutdown();
            }
        }
    }

    private static void forward() {
        m0a.low();
        m1a.low();
        m0b.high();
        m1b.high();
    }

    private static void backward() {
        m0b.low();
        m1b.low();
        m0a.high();
        m1a.high();
    }

    private static void stop() {
        m0a.low();
        m0b.low();
        m1a.low();
        m1b.low();
    }

}
