package org.apache.coyote.http11.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.coyote.http11.message.request.Http11RequestParser;
import org.apache.coyote.http11.message.request.HttpRequest;

public class Http11InputBuffer {

    private static final int END_SIGN_FOR_STREAM = -1;

    private final InputStream inputStream;
    private final Charset defaultHeaderCharset;
    private final Charset defaultBodyCharset;

    public Http11InputBuffer(InputStream inputStream, Charset defaultHeaderCharset, Charset defaultBodyCharset) {
        this.inputStream = inputStream;
        this.defaultHeaderCharset = defaultHeaderCharset;
        this.defaultBodyCharset = defaultBodyCharset;
    }

    public HttpRequest read() throws IOException {
        Http11RequestParser parser = new Http11RequestParser(this, defaultHeaderCharset, defaultBodyCharset);
        return parser.parseRequest();
    }

    public String readLine() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int readByte;
        boolean seenCR = false;

        while ((readByte = inputStream.read()) != END_SIGN_FOR_STREAM) {
            if (readByte == '\r') {
                seenCR = true;
                continue;
            }
            if (seenCR && readByte == '\n') {
                break;
            }
            if (seenCR) {
                buffer.write('\r');
                seenCR = false;
            }
            buffer.write(readByte);
        }

        if (readByte == END_SIGN_FOR_STREAM && buffer.size() == 0) {
            return null;
        }
        return buffer.toString(defaultHeaderCharset);
    }

    public byte[] readNBytes(int length) throws IOException {
        return inputStream.readNBytes(length);
    }
}
