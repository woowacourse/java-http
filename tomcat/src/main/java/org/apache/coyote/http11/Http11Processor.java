package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HandlerAdapter;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeaders;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String EMPTY_INPUT = "";

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

            final HttpRequest httpRequest = createHttpRequest(inputStream);
            final RequestHandler requestHandler = findHandler(httpRequest);
            final HttpResponse httpResponse = requestHandler.handle(httpRequest);

            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestHandler findHandler(final HttpRequest httpRequest) {
        final HandlerAdapter handlerAdapter = new HandlerAdapter();
        return handlerAdapter.find(httpRequest);
    }

    private HttpRequest createHttpRequest(final InputStream inputStream) throws IOException {
        final InputStreamReader reader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final String startLine = bufferedReader.readLine();
        final HttpRequestStartLine requestStartLine = HttpRequestStartLine.from(startLine);
        final HttpRequestHeaders httpRequestHeaders = getRequestHeader(bufferedReader);

        if (!httpRequestHeaders.contains(CONTENT_LENGTH_HEADER)) {
            return HttpRequest.of(requestStartLine, httpRequestHeaders);
        }

        final HttpRequestBody httpRequestBody = getRequestBody(bufferedReader, httpRequestHeaders);
        return HttpRequest.of(requestStartLine, httpRequestHeaders, httpRequestBody);
    }

    private HttpRequestHeaders getRequestHeader(final BufferedReader bufferedReader) throws IOException {
        String line;
        final List<String> headers = new ArrayList<>();
        while (!(line = bufferedReader.readLine()).equals(EMPTY_INPUT)) {
            headers.add(line);
        }
        return HttpRequestHeaders.from(headers);
    }

    private HttpRequestBody getRequestBody(final BufferedReader bufferedReader,
                                           final HttpRequestHeaders httpRequestHeaders)
            throws IOException {
        final int contentLength = Integer.parseInt(httpRequestHeaders.getValue(CONTENT_LENGTH_HEADER));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);

        return new HttpRequestBody(requestBody);
    }
}
