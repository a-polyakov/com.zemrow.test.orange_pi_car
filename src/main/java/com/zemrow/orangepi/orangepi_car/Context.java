package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;

import java.io.IOException;

public class Context {
    private final GpioController gpio;
    private final I2CBus i2c;
    private final PiCar car;

    public Context() throws PlatformAlreadyAssignedException, IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {
        System.out.println(System.currentTimeMillis() + " Init");
        PlatformManager.setPlatform(Platform.ORANGEPI);
        gpio = GpioFactory.getInstance();
        System.out.println(System.currentTimeMillis() + " GPIO");
        i2c = I2CFactory.getInstance(I2CBus.BUS_0);
        System.out.println(System.currentTimeMillis() + " I2C");
        I2CDevice device = i2c.getDevice(PCA9685Helper.I2C_ADDRESS);
        PCA9685Helper.setPWMFreq(device, 60);
        System.out.println(System.currentTimeMillis() + " PCA9685");
        car = new PiCar(gpio, device);
        System.out.println(System.currentTimeMillis() + " Car");
    }

    public PiCar getCar() {
        return car;
    }

    public void shutdown(){
        if (i2c != null) {
            try {
                i2c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (gpio != null) {
            try {
                gpio.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
