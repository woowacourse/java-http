package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.headers.RequestHeaders;
import org.apache.coyote.http11.request.line.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);
    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String LOGIN_PAGE = "/login.html";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        LOG.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }
            final RequestLine requestLine = RequestLine.from(firstLine);
            final RequestHeaders requestHeader = getHeaders(bufferedReader);
            final RequestBody requestBody = getBody(bufferedReader, requestHeader);
//
//            final ResponseEntity responseEntity = handleRequest(requestLine, requestHeader, requestBody);

//            final String response = httpResponseGenerator.generate(responseEntity);
//            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private RequestHeaders getHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaders = readRequestHeaders(bufferedReader);
        return RequestHeaders.from(requestHeaders);
    }

    private List<String> readRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaders = new ArrayList<>();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            requestHeaders.add(line);
        }
        return requestHeaders;
    }

    private RequestBody getBody(final BufferedReader bufferedReader, final RequestHeaders requestHeaders)
            throws IOException {
        int contentLength = Integer.parseInt(requestHeaders.headers().get("Content-Length").get(0));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

}
