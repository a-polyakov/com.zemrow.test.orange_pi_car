package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;

import java.io.IOException;

public class Test3 {



    public static void main(String[] args) throws PlatformAlreadyAssignedException, InterruptedException, IOException, I2CFactory.UnsupportedBusNumberException {
        PlatformManager.setPlatform(Platform.ORANGEPI);
        GpioController gpio = null;
        I2CBus i2c = null;
        try {
            gpio = GpioFactory.getInstance();
            i2c = I2CFactory.getInstance(I2CBus.BUS_0);
            I2CDevice device = i2c.getDevice(PCA9685Helper.I2C_ADDRESS);
            PCA9685Helper.setPWMFreq(device, 60);
            final PiCar car = new PiCar(gpio, device);

            System.out.println("forward");
            car.setSpeed(100);
            Thread.sleep(500);

            System.out.println("stop");
            car.setSpeed(0);
            Thread.sleep(500);

            System.out.println("backward");
            car.setSpeed(-100);
            Thread.sleep(500);

            System.out.println("stop");
            car.setSpeed(0);
            Thread.sleep(500);

            for (int i = 0; i < 250; i += 5) {
                System.out.println("helm " + i);
                car.setHelm(200+i);
                Thread.sleep(500);
                car.setHelm(700-i);
                Thread.sleep(500);
            }
            car.setHelm(250);

            for (int i = -90; i < 90; i += 3) {
                System.out.println("cam " + i);
                car.setCamLeftRight(i);
                Thread.sleep(500);
            }
            car.setCamLeftRight(0);

            for (int i = 0; i < 90; i += 1) {
                System.out.println("cam top " + i);
                car.setCamTopButton(i);
                Thread.sleep(500);
            }
            car.setCamTopButton(0);

            i2c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (i2c != null) {
                    i2c.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            if (gpio != null) {
                gpio.shutdown();
            }
        }
    }
}
