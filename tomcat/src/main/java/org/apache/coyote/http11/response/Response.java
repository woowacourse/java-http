package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.request.Request.HEADER_KEY_VALUE_DELIMITER;
import static org.apache.coyote.http11.request.Request.SPACE_DELIMITER;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.coyote.http11.Protocol;
import org.apache.coyote.http11.URL;

public class Response {

    private static final String EMPTY_STRING = "";

    private final OutputStream outputStream;
    private final ResponseHeaders headers;

    private Response(final OutputStream outputStream, final ResponseHeaders headers) {
        this.outputStream = outputStream;
        this.headers = headers;
    }

    public static Response of(final OutputStream outputStream) {
        return new Response(outputStream, new ResponseHeaders((new HashMap<>())));
    }

    public void addHeader(final String key, final String value) {
        this.headers.add(key, value);
    }

    public void write(final HttpStatus status) throws IOException, URISyntaxException {
        write(status, EMPTY_STRING);
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
                Protocol.HTTP1_1.getValue() + SPACE_DELIMITER + status.getStatusNumber()
                        + SPACE_DELIMITER + status.getStatusName() + SPACE_DELIMITER,
                headerToString(),
                EMPTY_STRING,
                content);
        outputStream.write(body.getBytes());
        outputStream.flush();
    }

    private String headerToString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> entry : headers.getHeaders().entrySet()) {
            builder.append(entry.getKey()).append(HEADER_KEY_VALUE_DELIMITER + SPACE_DELIMITER).append(entry.getValue()).append(" \r\n");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }

    public ResponseHeaders getHeaders() {
        return headers;
    }
}
