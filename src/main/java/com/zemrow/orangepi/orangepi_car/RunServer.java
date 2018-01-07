package com.zemrow.orangepi.orangepi_car;

import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

public class RunServer {
    public static void main(String[] args) throws IOException, InterruptedException, I2CFactory.UnsupportedBusNumberException, PlatformAlreadyAssignedException {
        final Context context = new Context();
        final HttpServer server = HttpServer.create(new InetSocketAddress(NetworkConst.PORT), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if ("/".equals(httpExchange.getRequestURI().toString())) {
                    if (NetworkConst.METHOD_GET.equals(httpExchange.getRequestMethod())) {
                        try (final InputStream inputStream = getClass().getResourceAsStream("/index.html")) {
                            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            final int size = IOUtils.copy(inputStream, outputStream);
                            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, size);
                            outputStream.writeTo(httpExchange.getResponseBody());
                        }
                    } else if (NetworkConst.METHOD_POST.equals(httpExchange.getRequestMethod())) {
                        try {
                            context.getCar().read(new InputStreamReader(httpExchange.getRequestBody()));
                            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        } catch (Exception e) {
                            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            e.printStackTrace(new PrintWriter(outputStream));
                            final int size = outputStream.size();
                            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, size);
                            outputStream.writeTo(httpExchange.getResponseBody());
                        }
                    } else {
                        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
                    }
                } else {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                }
                httpExchange.close();
            }
        });
        server.start();
        System.out.println(System.currentTimeMillis() + " ServerSocket start, port:" + NetworkConst.PORT);
    }
}
