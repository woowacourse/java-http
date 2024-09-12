package support;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class StubSocket extends Socket {

    private final String request;
    private final ByteArrayOutputStream outputStream;
    private boolean throwIOException;

    public StubSocket(final String request) {
        this.request = request;
        this.throwIOException = false;
        this.outputStream = new ByteArrayOutputStream();
    }

    public StubSocket() {
        this("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
    }

    public void setThrowIOException(boolean throwIOException) {
        this.throwIOException = throwIOException;
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

    public InputStream getInputStream() throws IOException {
        if (throwIOException) {
            throw new IOException("TEST IOException");
        }
        return new ByteArrayInputStream(request.getBytes());
    }

    public OutputStream getOutputStream() throws IOException {
        if (throwIOException) {
            throw new IOException("TEST IOException");
        }
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
