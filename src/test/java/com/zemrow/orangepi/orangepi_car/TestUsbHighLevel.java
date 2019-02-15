package com.zemrow.orangepi.orangepi_car;

import com.zemrow.orangepi.orangepi_car.joystick.UsbHelm;

import javax.usb.*;
import javax.usb.event.*;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class TestUsbHighLevel {

    public static void main(String[] args) throws InterruptedException, UsbException, UnsupportedEncodingException {

        // Получить сервис для работы с USB
        final UsbServices services = UsbHostManager.getUsbServices();
//        System.out.println("USB Service Implementation: " + services.getImpDescription());
//        System.out.println("Implementation version: " + services.getImpVersion());
//        System.out.println("Service API version: " + services.getApiVersion());
//        System.out.println();

        // Добавить слушателя событий USB сервиса
        services.addUsbServicesListener(new UsbServicesListener() {
            @Override
            public void usbDeviceAttached(UsbServicesEvent event) {
                // событие подключения usb устройства, при первом добавлении слушателя вызывается для всех уже подключенных устройств
//                System.out.println("services usbDeviceAttached");
//                System.out.println(event.getUsbDevice().getUsbDeviceDescriptor());
            }

            @Override
            public void usbDeviceDetached(UsbServicesEvent event) {
                // событие отключения usb устройства
//                System.out.println("services usbDeviceDetached");
            }
        });

        // Корневой элемент в иерархии USB устройств
        final UsbHub usbHub = services.getRootUsbHub();

        final UsbDevice device = findDevice(usbHub, UsbHelm.VID, UsbHelm.PID);
//        System.out.println(device.getUsbDeviceDescriptor());
//        Device Descriptor:
//        bLength                 18
//        bDescriptorType          1
//        bcdUSB                1.10
//        bDeviceClass             3 HID
//        bDeviceSubClass          0
//        bDeviceProtocol          0
//        bMaxPacketSize0          8
//        idVendor            0x0458
//        idProduct           0x3017
//        bcdDevice             1.20
//        iManufacturer            1
//        iProduct                 2
//        iSerial                  0
//        bNumConfigurations       1

        device.addUsbDeviceListener(new UsbDeviceListener() {
            @Override
            public void usbDeviceDetached(UsbDeviceEvent event) {
                System.out.println("device usbDeviceDetached");
            }

            @Override
            public void errorEventOccurred(UsbDeviceErrorEvent event) {
                System.out.println("device errorEventOccurred");
            }

            @Override
            public void dataEventOccurred(UsbDeviceDataEvent event) {
                System.out.println("device dataEventOccurred");
            }
        });

//        попытка подключиться на прослушивание прерываний с устройства не получилась, надо разбираться в параметрах
//        UsbControlIrp irp = device.createUsbControlIrp(
//                (byte) (UsbConst.REQUESTTYPE_DIRECTION_IN
//                        | UsbConst.REQUESTTYPE_TYPE_STANDARD
//                        | UsbConst.REQUESTTYPE_RECIPIENT_DEVICE),
//                UsbConst.REQUEST_CLEAR_FEATURE,
//                (short) 0,
//                (short) 0
//        );
//        irp.setData(new byte[3]);
//        device.syncSubmit(irp);
//        System.out.println(irp.getData()[0]);
//        System.out.println(irp.getData()[1]);
//        System.out.println(irp.getData()[2]);

        UsbConfiguration configuration = device.getActiveUsbConfiguration();
//        System.out.println(configuration.getUsbConfigurationDescriptor());
//        Configuration Descriptor:
//        bLength                  9
//        bDescriptorType          2
//        wTotalLength            34
//        bNumInterfaces           1
//        bConfigurationValue      1
//        iConfiguration           0
//        bmAttributes          0x80
//        (Bus Powered)
//        bMaxPower              350mA

        final List<UsbInterface> usbInterfaces = configuration.getUsbInterfaces();
        if (usbInterfaces != null) {
            for (UsbInterface usbInterface : usbInterfaces) {
//                System.out.println(usbInterface.getUsbInterfaceDescriptor());
//                Interface Descriptor:
//                bLength                  9
//                bDescriptorType          4
//                bInterfaceNumber         0
//                bAlternateSetting        0
//                bNumEndpoints            1
//                bInterfaceClass          3 HID
//                bInterfaceSubClass       0
//                bInterfaceProtocol       0
//                iInterface               0

                System.out.println("usbInterface isClaimed " + usbInterface.isClaimed());
                usbInterface.claim(new UsbInterfacePolicy() {
                    @Override
                    public boolean forceClaim(UsbInterface usbInterface) {
                        System.out.println("usbInterface forceClaim");
                        return true;
                    }
                });
                try {
                    final List<UsbEndpoint> usbEndpoints = usbInterface.getUsbEndpoints();
                    if (usbEndpoints != null) {
                        for (UsbEndpoint endpoint : usbEndpoints) {
//                            System.out.println(endpoint.getUsbEndpointDescriptor());
//                            Endpoint Descriptor:
//                            bLength                  7
//                            bDescriptorType          5
//                            bEndpointAddress      0x81  EP 1 IN
//                            bmAttributes             3
//                            Transfer Type             Interrupt
//                            Synch Type                None
//                            Usage Type                Data
//                            wMaxPacketSize           6
//                            bInterval               10

//                           TODO LibUsb.interruptTransfer

                            UsbPipe pipe = endpoint.getUsbPipe();

                            pipe.open();

                            pipe.addUsbPipeListener(new UsbPipeListener() {
                                @Override
                                public void errorEventOccurred(UsbPipeErrorEvent event) {
                                    System.out.println("pipe errorEventOccurred");
                                    UsbException error = event.getUsbException();
                                    System.out.println("... Handle error ..." + error);
                                }

                                @Override
                                public void dataEventOccurred(UsbPipeDataEvent event) {
                                    byte[] data = event.getData();
                                    int angle = Byte.toUnsignedInt(data[0]);
                                    System.out.print("Angle " + angle);
                                    int speed = Byte.toUnsignedInt(data[1]);
                                    System.out.print(" speed " + speed);
                                    int button = Byte.toUnsignedInt(data[3]);
                                    System.out.println(" button " + Integer.toBinaryString(button));
                                }
                            });

                            final byte[] data = new byte[6];
                            pipe.syncSubmit(data);
                            System.out.println(data);

                            int i = 1;
                            while (i == 1) {
                                UsbIrp usbIrp = pipe.createUsbIrp();
                                usbIrp.setAcceptShortPacket(true);
                                usbIrp.setData(data);
                                pipe.syncSubmit(usbIrp);
                            }


                            Thread.sleep(60000);

//                            final UsbIrp usbIrp = pipe.createUsbIrp();
//                            usbIrp.setData(new byte[3]);
//                            pipe.asyncSubmit(usbIrp);
//                            System.out.println(usbIrp.getData()[0]);
//                            System.out.println(usbIrp.getData()[1]);
//                            System.out.println(usbIrp.getData()[2]);

//                            final byte[] data = new byte[3];
//                            pipe.syncSubmit(data);
//                            System.out.println(data[0]);
//                            System.out.println(data[1]);
//                            System.out.println(data[2]);

                            UsbControlIrp irp = device.createUsbControlIrp(
                                    (byte) (UsbConst.REQUESTTYPE_DIRECTION_IN | UsbConst.REQUESTTYPE_RECIPIENT_ENDPOINT),
//                                    UsbConst.REQUEST_SYNCH_FRAME,
//                                    UsbConst.REQUEST_CLEAR_FEATURE,
                                    UsbConst.REQUEST_SET_CONFIGURATION,
                                    (short) 0,
                                    endpoint.getUsbEndpointDescriptor().bEndpointAddress()
                            );
                            irp.setData(new byte[3]);
                            device.syncSubmit(irp);
                            System.out.println(irp.getData()[0]);
                            System.out.println(irp.getData()[1]);
                            System.out.println(irp.getData()[2]);


//                            System.out.println("Active: "+pipe.isActive());

//                            pipe.syncSubmit(new byte[0]);

                            Thread.sleep(60000);
                            pipe.abortAllSubmissions();
                            pipe.close();
                        }
                    }
                } finally {
                    usbInterface.release();
                }
            }
        }


    }

    /**
     * Найти устройство, поиск производится по всей иерархии
     *
     * @param hub       откуда начинать искать
     * @param vendorId  идентификатор производителя устройства которое ищем
     * @param productId идентификатор устройствакоторое ищем
     * @return найденной устройство или null если не смогли найти
     */
    public static UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
    }
}
