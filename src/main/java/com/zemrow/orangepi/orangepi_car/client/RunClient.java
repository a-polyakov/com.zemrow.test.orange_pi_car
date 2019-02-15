package com.zemrow.orangepi.orangepi_car.client;

import com.zemrow.orangepi.orangepi_car.Car;
import com.zemrow.orangepi.orangepi_car.joystick.JoystickListener;
import com.zemrow.orangepi.orangepi_car.joystick.UsbHelm;

import java.io.Console;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RunClient {
    public static void main(String[] args) throws Exception {
        final UsbHelm helm = new UsbHelm(new JoystickListener() {
            @Override
            public void update(int helm, int speed, int camLeftRight, int camTopButton) {
                System.out.println("helm " + helm + " speed " + speed + " camLeftRight " + camLeftRight + " camTopButton " + camTopButton);

                try {
                    URL url = new URL("http://192.168.1.71:8181/");
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-type", "application/json");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    try (OutputStream os = connection.getOutputStream()) {
                        final StringBuilder json = new StringBuilder();
                        json.append('{');
                        json.append(Car.SPEED).append(':').append(speed).append(',');
                        json.append(Car.HELM).append(':').append(helm).append(',');
                        json.append(Car.CAM_LEFT_RIGHT).append(':').append(camLeftRight).append(',');
                        json.append(Car.CAM_TOP_BUTTON).append(':').append(camTopButton);
                        json.append('}');
                        os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    connection.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        helm.init();
        Console console = System.console();
        if (console != null) {
            console.readLine();
        } else {
            System.in.read();
        }
    }
}
