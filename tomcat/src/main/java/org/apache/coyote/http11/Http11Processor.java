package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.SPACE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.auth.HttpCookie;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestURI;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int HTTP_METHOD_INDEX = 0;

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var firstLine = bufferedReader.readLine();

            final var requestHeader = readHeader(bufferedReader);
            final var requestURI = readRequestURI(firstLine, requestHeader, bufferedReader);
            final var httpCookie = HttpCookie.from(requestHeader.get("Cookie"));

            final var responseEntity = ResponseEntity.from(requestURI);
            final var response = responseEntity.getResponse(httpCookie);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestHeader readHeader(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String line = bufferedReader.readLine(); !"".equals(line); line = bufferedReader.readLine()) {
            stringBuilder.append(line).append(CRLF);
        }

        return RequestHeader.from(stringBuilder.toString());
    }

    private static RequestURI readRequestURI(
            final String firstLine,
            final RequestHeader requestHeader,
            final BufferedReader bufferedReader
    ) throws IOException {
        final var requestURIElements = parseRequestURIElements(firstLine);
        final var httpMethod = HttpMethod.valueOfHttpStatus(requestURIElements.get(HTTP_METHOD_INDEX));

        if (httpMethod == HttpMethod.GET) {
            return getRequestURI(requestURIElements);
        }

        return postRequestURI(requestURIElements, parseRequestBody(requestHeader, bufferedReader));
    }

    private static List<String> parseRequestURIElements(final String requestURILine) {
        return Arrays.stream(requestURILine.split(SPACE))
                .collect(Collectors.toUnmodifiableList());
    }

    private static RequestURI getRequestURI(final List<String> requestURIElements) {
        return RequestURI.get(
                requestURIElements,
                user -> log.info("user : {}", user)
        );
    }

    private static String parseRequestBody(
            final RequestHeader requestHeader,
            final BufferedReader bufferedReader
    ) throws IOException {
        int contentLength = Integer.parseInt(requestHeader.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static RequestURI postRequestURI(final List<String> requestURIElements, final String requestBody) {
        return RequestURI.post(
                requestURIElements,
                requestBody,
                user -> log.info("user : {}", user)
        );
    }

}
