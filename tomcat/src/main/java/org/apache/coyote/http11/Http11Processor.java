package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, org.apache.coyote.Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String url = getUrl(bufferedReader);
            UrlResponse urlResponse = ProcessorManager.getUrlResponse(url);

            String http11Response = getHttp11Response(urlResponse);

            outputStream.write(http11Response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getUrl(BufferedReader bufferedReader) throws IOException {
        String urlLine = bufferedReader.readLine();
        Objects.requireNonNull(urlLine);
        return urlLine.split(" ")[1];
    }

    private String getHttp11Response(UrlResponse urlResponse) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + urlResponse.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + urlResponse.getResponseBody().getBytes().length + " ",
                "",
                urlResponse.getResponseBody());
    }
}
