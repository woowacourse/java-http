package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.ContentType;
import org.apache.coyote.Processor;
import org.apache.coyote.support.ResourcesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final int URL_INDEX = 1;
    public static final String LANDING_PAGE_URL = "/";
    public static final String STATIC_PATH = "static";
    public static final String EXTENSION_DELIMITER = ".";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String uri = parseUri(bufferedReader);
            String responseBody = accessUri(uri);
            ContentType contentType = parseContentType(uri);

            String response = toResponse(contentType, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUri(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine().split(" ", -1)[URL_INDEX];
    }

    private String accessUri(final String uri) throws IOException {
        if (uri.equals(LANDING_PAGE_URL)) {
            return "Hello world!";
        }
        return ResourcesUtil.readResource(STATIC_PATH + uri);
    }

    private ContentType parseContentType(final String uri) {
        String extension = ResourcesUtil.parseExtension(uri);
        return ContentType.fromExtension(extension);
    }

    private String toResponse(final ContentType contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
