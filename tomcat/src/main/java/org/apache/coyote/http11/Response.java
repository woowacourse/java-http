package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.coyote.HttpStatus;

public class Response {

    private final OutputStream outputStream;
    private final Headers headers;

    private Response(final OutputStream outputStream, final Headers headers) {
        this.outputStream = outputStream;
        this.headers = headers;
    }

    public static Response of(final OutputStream outputStream) {
        return new Response(outputStream, new Headers(new HashMap<>()));
    }

    public void addHeader(final String key, final String value) {
        this.headers.add(key, value);
    }

    public void write(final HttpStatus status) throws IOException, URISyntaxException {
        write(status, "");
    }

    public void write(final HttpStatus status, final String path) throws IOException, URISyntaxException {
        final URL url = URL.of(path);
        write(status, url);
    }

    public void write(final HttpStatus status, final URL url) throws IOException, URISyntaxException {
        final String content = url.read();
        this.headers.add("Content-Type", url.getMIMEType() + ";charset=utf-8");
        this.headers.add("Content-Length", String.valueOf(content.getBytes().length));
        final String body = String.join("\r\n",
                "HTTP/1.1 "+status.getStatusNumber()+" "+status.getStatusName()+" ",
                headerToString(),
                "",
                content);
        outputStream.write(body.getBytes());
        outputStream.flush();
    }

    private String headerToString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> entry : headers.getHeaders().entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }
}
