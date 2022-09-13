package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.request.Request.SPACE_DELIMITER;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
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

    public void redirect(final HttpStatus status, final String redirectUri) throws IOException, URISyntaxException {
        headers.add("Location", redirectUri);
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
                headers.headerToString(),
                EMPTY_STRING,
                content);
        outputStream.write(body.getBytes());
        outputStream.flush();
    }
}
