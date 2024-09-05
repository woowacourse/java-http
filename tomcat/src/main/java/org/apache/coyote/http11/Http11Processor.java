package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Dispatcher dispatcher;
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        dispatcher = Dispatcher.getInstance();
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            HttpRequest httpRequest = parseHttpRequest(bufferedReader);
            HttpRequestHandler httpRequestHandler = dispatcher.mappedHandler(httpRequest);
            HttpResponse httpResponse = httpRequestHandler.handle(httpRequest);

            httpResponse.flush(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseHttpRequest(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        HttpHeaders httpHeaders = readHeaders(bufferedReader);
        HttpMessageBody httpMessageBody = readHttpMessageBody(bufferedReader, httpHeaders);

        return new HttpRequest(requestLine, httpHeaders, httpMessageBody);
    }

    private HttpHeaders readHeaders(final BufferedReader bufferedReader) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
            httpHeaders.putHeader(headerLine);
        }
        return httpHeaders;
    }

    private HttpMessageBody readHttpMessageBody(
            final BufferedReader bufferedReader,
            final HttpHeaders httpHeaders
    ) throws IOException {
        String lengthValue = httpHeaders.getHeaderValue("Content-Length");
        if (lengthValue == null) {
            return HttpMessageBody.createEmptyBody();
        }

        int bodyLength = Integer.parseInt(lengthValue);
        char[] buffer = new char[bodyLength];
        bufferedReader.read(buffer, 0, bodyLength);
        String requestBody = new String(buffer);
        return new HttpMessageBody(requestBody);
    }
}
