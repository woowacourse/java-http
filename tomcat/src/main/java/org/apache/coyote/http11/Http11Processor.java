package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.apache.servlet.Servlet;
import org.apache.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String REQUEST_HEADER_SPLITTER = ": ";
    private static final int REQUEST_HEADER_KEY_INDEX = 0;
    private static final int RESPONSE_HEADER_KEY_INDEX = 1;
    private static final String CONTENT_LENGTH = "Content-Length";

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
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = generateHttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            Servlet servlet = ServletContainer.findByPath(httpRequest.getPath());
            servlet.service(httpRequest, httpResponse);

            write(outputStream, httpResponse);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest generateHttpRequest(final BufferedReader bufferedReader) throws IOException {
        HttpRequest request = new HttpRequest(Objects.requireNonNull(bufferedReader.readLine()));
        RequestLine requestLine = request.getRequestLine();
        log.info("{} {} {}", requestLine.getHttpMethod(), requestLine.getRequestURI().getPath(),
                requestLine.getHttpVersion().getValue());

        addHeaders(bufferedReader, request);
        addBody(bufferedReader, request);
        return request;
    }

    private void addHeaders(final BufferedReader bufferedReader, final HttpRequest request) throws IOException {
        String line;
        while (!(line = Objects.requireNonNull(bufferedReader.readLine())).isBlank()) {
            String[] splitHeader = line.split(REQUEST_HEADER_SPLITTER);
            request.addHeader(splitHeader[REQUEST_HEADER_KEY_INDEX], splitHeader[RESPONSE_HEADER_KEY_INDEX]);
        }
    }

    private void addBody(final BufferedReader bufferedReader, final HttpRequest request) throws IOException {
        if (request.hasHeader(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(request.getHeader(CONTENT_LENGTH));
            char[] body = new char[contentLength];
            bufferedReader.read(body, 0, contentLength);
            request.addBody(String.copyValueOf(body));
        }
    }

    private void write(final OutputStream outputStream, final HttpResponse httpResponse) throws IOException {
        String response = httpResponse.parseResponse();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
