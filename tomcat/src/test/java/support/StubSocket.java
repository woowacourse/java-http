package support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class StubSocket extends Socket {

    private final String request;
    private final int maximumReadSize;
    private final ByteArrayOutputStream outputStream;

    public StubSocket(final String request) {
        this(request, Integer.MAX_VALUE);
    }

    public StubSocket(final String request, final int maximumReadSize) {
        this.request = request;
        this.maximumReadSize = maximumReadSize;
        this.outputStream = new ByteArrayOutputStream();
    }

    public StubSocket() {
        this("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
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
        return new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public int read(final byte[] bytes, final int offset, final int length) {
                return super.read(bytes, offset, Math.min(length, maximumReadSize));
            }

            @Override
            public int available() {
                return 0;
            }
        };
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

    public byte[] outputBytes() {
        return outputStream.toByteArray();
    }
}
