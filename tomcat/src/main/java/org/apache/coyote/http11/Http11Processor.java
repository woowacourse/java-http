package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.url.HandlerMapping;
import org.apache.coyote.http11.url.Url;
import org.apache.coyote.http11.utils.UrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

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
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String request = bufferedReader.readLine();

            String uri = UrlParser.extractUri(request);
            String httpMethod = UrlParser.extractMethod(request);
            Url url = HandlerMapping.from(uri);
            Http11Response responseBody = Http11Response.extract(url, httpMethod);

            final var response = createResponse(responseBody.getHttpStatus(), responseBody.getContentType(),
                    responseBody.getResource());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getUri(final BufferedReader bufferedReader) {
        String uri = "";
        try {
            uri = bufferedReader.readLine()
                    .split(" ")[1]
                    .substring(1);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return uri;
    }

    private String createResponse(HttpStatus httpStatus, String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getName() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
