package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.http11.handler.RequestMapping;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeaders;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HEADER_DELIMITER = ":";

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
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = createHttpRequest(reader);
            final HttpResponse httpResponse = createHttpResponse(httpRequest);

            outputStream.write(httpResponse.createResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final BufferedReader reader) throws IOException {
        final HttpRequestLine httpRequestLine = HttpRequestLine.from(reader.readLine());
        final HttpRequestHeaders httpHeaders = new HttpRequestHeaders(parseRequestHeaders(reader));
        final HttpRequestBody httpRequestBody = HttpRequestBody.from(
                parseRequestBody(httpRequestLine.getMethod(), httpHeaders.getContentLength(), reader));
        return new HttpRequest(httpRequestLine, httpHeaders, httpRequestBody);
    }

    private Map<String, String> parseRequestHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            final String[] header = line.split(HEADER_DELIMITER);
            final String name = header[0].trim();
            final String value = header[1].trim();
            headers.put(name, value);
        }
        return headers;
    }

    private String parseRequestBody(final HttpMethod method, final int contentLength, final BufferedReader reader)
            throws IOException {
        if (method.isPost() && contentLength > 0) {
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    private HttpResponse createHttpResponse(final HttpRequest httpRequest) throws IOException {
        final RequestMapping requestMapping = new RequestMapping();
        final Controller controller = requestMapping.getController(httpRequest);
        return controller.service(httpRequest);
    }
}
