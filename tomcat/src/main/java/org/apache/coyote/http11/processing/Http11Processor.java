package org.apache.coyote.http11.processing;

import com.techcourse.app.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.io.HttpRequestReader;
import org.apache.coyote.http11.io.HttpResponseWriter;
import org.apache.coyote.http11.message.HttpResponseMessage;
import org.apache.coyote.http11.protocol.enums.HeaderKey;
import org.apache.coyote.http11.protocol.request.HttpRequest;
import org.apache.coyote.http11.protocol.request.RequestBody;
import org.apache.coyote.http11.protocol.request.RequestHeaders;
import org.apache.coyote.http11.protocol.request.RequestLine;
import org.apache.coyote.http11.protocol.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Dispatcher dispatcher;

    public Http11Processor(
            Socket connection,
            Dispatcher dispatcher
    ) {
        this.connection = connection;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStreamReader = new InputStreamReader(connection.getInputStream());
             final var outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
            HttpRequestReader httpRequestReader = new HttpRequestReader(new BufferedReader(inputStreamReader));
            HttpRequest httpRequest = readHttpRequest(httpRequestReader);
            HttpResponse httpResponse = HttpResponse.ok();
            dispatcher.dispatch(httpRequest, httpResponse);
            HttpResponseWriter httpResponseWriter = new HttpResponseWriter(outputStreamWriter);
            httpResponseWriter.write(HttpResponseMessage.from(httpResponse));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(HttpRequestReader httpRequestReader) throws IOException {
        RequestLine requestLine = new RequestLine(httpRequestReader.readLine());
        RequestHeaders requestHeaders = new RequestHeaders(readHttpHeaders(httpRequestReader));
        RequestBody requestBody = new RequestBody(readRequestBody(httpRequestReader, requestHeaders));
        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private List<String> readHttpHeaders(HttpRequestReader httpRequestReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while (!StringUtils.isEmpty(line = httpRequestReader.readLine())) {
            headerLines.add(line);
        }

        return headerLines;
    }

    private String readRequestBody(HttpRequestReader httpRequestReader, RequestHeaders requestHeaders)
            throws IOException {
        String contentLengthHeader = requestHeaders.getHeader(HeaderKey.CONTENT_LENGTH.getKey());
        if (contentLengthHeader == null) {
            return StringUtils.EMPTY;
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        return httpRequestReader.read(contentLength);
    }
}
