package com.zemrow.orangepi.orangepi_car;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    public static String toString(InputStream inputStream) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4096];
        int read;
        while ((read = inputStream.read(buffer)) > 0) {
            result.write(buffer, 0, read);
        }
        return result.toString("UTF-8");
    }

    public static int copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        int size = 0;
        byte buffer[] = new byte[4096];
        int read;
        while ((read = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, read);
            size += read;
        }
        return size;
    }
}
