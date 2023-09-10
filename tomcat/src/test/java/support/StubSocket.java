package support;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class StubSocket extends Socket {

    private final InputStream inputStream;
    private final ByteArrayOutputStream outputStream;

    public StubSocket(final String request) {
        this.inputStream = createInputStream(request);
        this.outputStream = new ByteArrayOutputStream();
    }

    public StubSocket() {
        this("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
    }

    private InputStream createInputStream(String request) {
//        String delimiter = System.lineSeparator() + System.lineSeparator();
//        String requestBody = request.substring(request.lastIndexOf(delimiter) + delimiter.length());
//        byte[] requestBodyBytes = request.getBytes(StandardCharsets.UTF_8);
//        return new StringBufferInputStream(request);
        return new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
    }

    public InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException ignored) {
            return null;
        }
    }

    public int getPort() {
        return 8080;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return new OutputStream() {
            @Override
            public void write(int b) {
                outputStream.write(b);
            }
        };
    }

    public String output() {
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
