package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

public class RunServer implements HttpHandler {

    private final Context context;

    public RunServer(Context context) {
        this.context = context;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if ("/config".equals(httpExchange.getRequestURI().toString())) {
            if (NetworkConst.METHOD_GET.equals(httpExchange.getRequestMethod())) {
                sendStatic(httpExchange, "/config.html");
            } else if (NetworkConst.METHOD_POST.equals(httpExchange.getRequestMethod())) {
                try {
                    final JSONObject json = new JSONObject(new JSONTokener(httpExchange.getRequestBody()));
                    final String servoName = json.getString("servo_name");
                    final int min = json.getInt("min");
                    final int max = json.getInt("max");
                    final int angle = json.getInt("check_angle");
                    final PiCar car = context.getCar();
                    switch (servoName) {
                        case Car.HELM:
                            car.configHelm(min, max);
                            car.setHelm(angle);
                            break;
                        case Car.CAM_LEFT_RIGHT:
                            car.configCamLeftRight(min, max);
                            car.setCamLeftRight(angle);
                            break;
                        case Car.CAM_TOP_BUTTON:
                            car.configCamTopButton(min, max);
                            car.setCamTopButton(angle);
                            break;
                    }
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                } catch (Exception e) {
                    sendError(httpExchange, e);
                }
            } else if (NetworkConst.METHOD_PUT.equals(httpExchange.getRequestMethod())) {
                //TODO save
            } else {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        } else if ("/".equals(httpExchange.getRequestURI().toString())) {
            if (NetworkConst.METHOD_GET.equals(httpExchange.getRequestMethod())) {
                sendStatic(httpExchange, "/index.html");
            } else if (NetworkConst.METHOD_POST.equals(httpExchange.getRequestMethod())) {
                try {
                    context.getCar().read(new InputStreamReader(httpExchange.getRequestBody()));
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                } catch (Exception e) {
                    sendError(httpExchange, e);
                }
            } else {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        } else {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
        }
        httpExchange.close();
    }

    private void sendError(HttpExchange httpExchange, Exception e) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(outputStream));
        final int size = outputStream.size();
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, size);
        outputStream.writeTo(httpExchange.getResponseBody());
    }

    private void sendStatic(HttpExchange httpExchange, String resource) throws IOException {
        try (final InputStream inputStream = getClass().getResourceAsStream(resource)) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int size = IOUtils.copy(inputStream, outputStream);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, size);
            outputStream.writeTo(httpExchange.getResponseBody());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, I2CFactory.UnsupportedBusNumberException, PlatformAlreadyAssignedException {
        final Context context = new Context();
        final HttpServer server = HttpServer.create(new InetSocketAddress(NetworkConst.PORT), 0);
        server.createContext("/", new RunServer(context));
        server.start();
        System.out.println(System.currentTimeMillis() + " ServerSocket start, port:" + NetworkConst.PORT);
    }
}
