package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.HttpRequestHandler;
import nextstep.jwp.handler.RequestMapping;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestBody;
import nextstep.jwp.http.request.HttpRequestHeaders;
import nextstep.jwp.http.response.HttpResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;

    private final Socket connection;
    private final RequestMapping requestMapping = new RequestMapping(HTTP_VERSION);

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
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String requestLine = reader.readLine();

            HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(parseHttpRequestHeaders(reader));
            HttpRequestBody httpRequestBody = parseHttpRequestBody(reader, httpRequestHeaders);

            HttpRequest httpRequest = HttpRequest.of(requestLine, httpRequestHeaders, httpRequestBody);

            HttpRequestHandler handler = requestMapping.getHandler(httpRequest);
            HttpResponse response = handler.handleHttpRequest(httpRequest);

            outputStream.write(response.httpResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> parseHttpRequestHeaders(final BufferedReader reader) throws IOException {
        List<String> httpRequestHeaders = new ArrayList<>();
        String line = reader.readLine();
        if (line == null) {
            return httpRequestHeaders;
        }
        while (!"".equals(line)) {
            httpRequestHeaders.add(line);
            line = reader.readLine();
        }
        return httpRequestHeaders;
    }

    private HttpRequestBody parseHttpRequestBody(final BufferedReader reader,
                                                 final HttpRequestHeaders httpRequestHeaders)
            throws IOException {
        if (httpRequestHeaders.isContainContentLength()) {
            int contentLength = httpRequestHeaders.contentLength();
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return HttpRequestBody.from(new String(buffer));
        }
        return HttpRequestBody.empty();
    }
}
