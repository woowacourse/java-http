package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.controller.RequestMapping;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import org.apache.controller.Controller;
import org.apache.coyote.Processor;
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
            final var outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest httpRequest = extractRequest(bufferedReader);
            HttpResponse httpResponse = handle(httpRequest);

            outputStream.write(httpResponse.writeResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest extractRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        HttpHeaders httpHeaders = parseHttpHeaders(bufferedReader);
        String requestBody = parseRequestBody(bufferedReader, httpHeaders);

        return HttpRequest.of(requestLine, httpHeaders, requestBody);
    }

    private HttpHeaders parseHttpHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            lines.add(line);
        }
        return HttpHeaders.parse(lines);
    }

    private String parseRequestBody(BufferedReader bufferedReader, HttpHeaders httpHeaders)
        throws IOException {
        char[] body = new char[httpHeaders.getContentLength()];
        bufferedReader.read(body);
        return new String(body);
    }

    private HttpResponse handle(HttpRequest httpRequest) {
        Controller controller = RequestMapping.get(httpRequest);
        return controller.service(httpRequest);
    }
}
