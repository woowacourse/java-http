package org.apache.coyote.http11;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class HttpResponseBody {

    private final ByteArrayOutputStream outputStream;
    private final BufferedWriter writer;

    public HttpResponseBody() {
        this.outputStream = new ByteArrayOutputStream();
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public void write(final String body) throws IOException {
        writer.write(body);
        writer.flush();
    }

    public String asString() {
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    public byte[] getBody() {
        return outputStream.toByteArray();
    }

    public int getLength() {
        return outputStream.size();
    }

    public boolean isNotEmpty() {
        return getLength() != 0;
    }
}
