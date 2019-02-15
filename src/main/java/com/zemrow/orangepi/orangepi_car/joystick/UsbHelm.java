package com.zemrow.orangepi.orangepi_car.joystick;

import com.zemrow.orangepi.orangepi_car.Car;

import javax.usb.*;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;
import java.util.List;

public class UsbHelm {
    /**
     * The vendor ID of the "Genius"
     */
    public final static short VID = 1112; // 0x0458
    /**
     * The vendor ID of the "Speed wheel 3"
     */
    public final static short PID = 12311; // 0x3017

    // Наблюдаемые показатели крайне левое 17, центр 96, крайне правое 174
    private final static int MIN_ANGLE = 20; // 17
    private final static int MAX_ANGLE = 170; // 174

    // Наблюдаемые показатели нажат правый рычаг 17, нажат левый рычаг 197, нейтральное положение 109
    private final static int MIN_SPEED = 20; // 17
    private final static int MAX_SPEED = 195;  // 197

    private int angle;

    private int speed;

    private boolean button1;
    private boolean button2;
    private boolean button3;
    private boolean button4;
    private boolean button5;
    private boolean button6;
    private boolean button7;
    private boolean button8;

    private final JoystickListener joystickListener;

    public UsbHelm(JoystickListener joystickListener) {
        this.joystickListener = joystickListener;
    }

    public void init() throws UsbException {
        // Получить сервис для работы с USB
        final UsbServices services = UsbHostManager.getUsbServices();
        // Добавить слушателя событий USB сервиса
        services.addUsbServicesListener(new UsbServicesListener() {
            /**
             * событие подключения usb устройства, при первом добавлении слушателя вызывается для всех уже подключенных устройств
             * @param event
             */
            @Override
            public void usbDeviceAttached(UsbServicesEvent event) {
                final UsbDevice device = event.getUsbDevice();
                final UsbDeviceDescriptor deviceDescriptor = device.getUsbDeviceDescriptor();
                if (deviceDescriptor.idVendor() == VID && deviceDescriptor.idProduct() == PID) {
                    final UsbConfiguration configuration = device.getActiveUsbConfiguration();
                    final List<UsbInterface> usbInterfaces = configuration.getUsbInterfaces();
                    if (usbInterfaces != null && usbInterfaces.size() == 1) {
                        final UsbInterface usbInterface = usbInterfaces.get(0);

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    usbInterface.claim(new UsbInterfacePolicy() {
                                        @Override
                                        public boolean forceClaim(UsbInterface usbInterface) {
                                            return true;
                                        }
                                    });
                                    final List<UsbEndpoint> usbEndpoints = usbInterface.getUsbEndpoints();
                                    if (usbEndpoints != null && usbEndpoints.size() == 1) {
                                        final UsbEndpoint endpoint = usbEndpoints.get(0);
                                        final UsbPipe pipe = endpoint.getUsbPipe();

                                        pipe.open();
                                        final byte[] data = new byte[4];
                                        // TODO listener
                                        while (true) {
                                            pipe.syncSubmit(data);
                                            setData(data);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();


                    }
                }
            }

            @Override
            public void usbDeviceDetached(UsbServicesEvent event) {
                // событие отключения usb устройства
                System.out.println("services usbDeviceDetached");

            }
        });
    }

    private void setData(byte data[]) {
        angle = Byte.toUnsignedInt(data[0]);
        if (angle < MIN_ANGLE) {
            angle = MIN_ANGLE;
        } else if (angle > MAX_ANGLE) {
            angle = MAX_ANGLE;
        }
        speed = Byte.toUnsignedInt(data[1]);
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        } else if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
        int button = Byte.toUnsignedInt(data[3]);
        button1 = (button & 1) != 0;
        button2 = (button & 2) != 0;
        button3 = (button & 4) != 0;
        button4 = (button & 8) != 0;
        button5 = (button & 16) != 0;
        button6 = (button & 32) != 0;
        button7 = (button & 64) != 0;
        button8 = (button & 128) != 0;


        int camLeftRight = 0;
        if (button1) {
            camLeftRight = Car.CAM_LEFT_RIGHT_MIN / 2;
        } else if (button2) {
            camLeftRight = Car.CAM_LEFT_RIGHT_MAX / 2;
        }
        int camTopButton = 0;
        if (button3) {
            camTopButton = Car.CAM_TOP_BUTTON_MAX;
        } else if (button5) {
            camTopButton = 2 * Car.CAM_TOP_BUTTON_MAX / 3;
        } else if (button7) {
            camTopButton = Car.CAM_TOP_BUTTON_MAX / 3;
        }
        //TODO
        joystickListener.update((Car.HELM_MAX - Car.HELM_MIN) * (angle - MIN_ANGLE) / (MAX_ANGLE - MIN_ANGLE) + Car.HELM_MIN,
                100 - 200 * (speed - MIN_SPEED) / (MAX_SPEED - MIN_SPEED),
                camLeftRight, camTopButton);
    }

    @Override
    public String toString() {
        return "UsbHelm{" +
                "angle=" + angle +
                ", speed=" + speed +
                ", button1=" + button1 +
                ", button2=" + button2 +
                ", button3=" + button3 +
                ", button4=" + button4 +
                ", button5=" + button5 +
                ", button6=" + button6 +
                ", button7=" + button7 +
                ", button8=" + button8 +
                '}';
    }
}
