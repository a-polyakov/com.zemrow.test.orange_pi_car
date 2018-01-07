package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;

/**
 *
 */
public class Test1 {
    public static void main(String[] args) throws InterruptedException, PlatformAlreadyAssignedException {
        PlatformManager.setPlatform(Platform.ORANGEPI);
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput gpioPinDigitalOutput = gpio.provisionDigitalOutputPin(OrangePiPin.GPIO_04);
//        GpioPinDigitalInput gpioPinDigitalInput = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
//        gpioPinDigitalInput.addListener(new GpioPinListenerDigital() {
//            @Override
//            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
//                System.out.println("GPIO Pin changed" + gpioPinDigitalStateChangeEvent.getPin() + gpioPinDigitalStateChangeEvent.getState());
//                System.out.println("Sleeping 5s");
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("Unsleep");
//            }
//        });

        System.out.println("BEGIN");
        for (int i = 0; i < 100; i++) {
            System.out.println(System.currentTimeMillis()+" "+i);
            gpioPinDigitalOutput.toggle();
            Thread.sleep(500);
        }
        gpioPinDigitalOutput.unexport();
        gpio.shutdown();
        System.out.println("END");
    }
}