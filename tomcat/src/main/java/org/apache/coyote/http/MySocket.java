package org.apache.coyote.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MySocket extends Socket {

    private final HttpRequest requestHeader;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException ignored) {
            return null;
        }
    }

    public int getPort() {
        return 8080;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(requestHeader.toString().getBytes());
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String output() {
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
