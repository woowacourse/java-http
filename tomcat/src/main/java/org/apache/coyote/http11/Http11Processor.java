package org.apache.coyote.http11;

import static org.apache.coyote.http11.request.Method.GET;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    public static final String LINE_SEPARATOR = "\r\n";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final Http11Request request = Http11Request.from(readInputStream(inputStream));
            final Http11Response response = makeResponseOf(request);

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readInputStream(final InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final StringBuilder input = new StringBuilder();
        try (bufferedReader) {
            for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
                input.append(s);
                input.append(LINE_SEPARATOR);
            }
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }

        return input.toString();
    }

    private Http11Response makeResponseOf(final Http11Request request) throws IOException {
        if (request.getMethod() == GET) {
            if (request.getUri().equals("/")) {
                return new Http11Response("Hello world!");
            }

            final URL resource = getClass().getClassLoader().getResource("static" + request.getUri());
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return new Http11Response(responseBody);
        }

        throw new IllegalArgumentException("Invalid Request Uri");
    }
}
