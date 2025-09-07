package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.web.FrontController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final FrontController frontController;

    public Http11Processor(final Socket connection, final  FrontController frontController) {
        this.connection = connection;
        this.frontController = frontController;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream())
        {
            final Http11Request http11Request = extractRequest(inputStream);
            final Http11Response http11Response = frontController.service(http11Request);
            outputStream.write(http11Response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Request extractRequest(final InputStream inputStream) throws IOException{
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> startLineWithHeaders = extractRequestHeadersWithStartLine(bufferedReader);
        final String body = extractRequestBody(bufferedReader, startLineWithHeaders);

        return new Http11Request(startLineWithHeaders, body);
    }

    private List<String> extractRequestHeadersWithStartLine(final BufferedReader bufferedReader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        String requestLine;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
            requestHeaders.add(requestLine);
        }

        return requestHeaders;
    }

    private String extractRequestBody(final BufferedReader bufferedReader, final List<String> headers) throws IOException {
        //Content-Length 헤더가 있는지 봐야함.
        final int contentLength = headers.stream()
                .filter(header -> header.startsWith("Content-Length:"))
                .map(header -> header.split(":", 2)[1].trim())
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);

        if (contentLength == 0) {
            return "";
        }

        char[] bodyChars = new char[contentLength];
        int readCount = bufferedReader.read(bodyChars);

        if (readCount == -1) {
            return "";
        }

        return new String(bodyChars);
    }
}
