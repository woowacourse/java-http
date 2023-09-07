package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.Controller;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var outputStream = connection.getOutputStream();
             final var br = new BufferedReader(
                     new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            final List<String> requestHeaderStrings = getRequestHeader(br);
            final String requestBodyString = parseRequestBodyString(requestHeaderStrings, br);

            final HttpRequest httpRequest = HttpRequest.of(requestHeaderStrings, requestBodyString);
            final HttpResponse httpResponse = HttpResponse.of(httpRequest.getParsedRequestURI());

            final RequestMapping requestMapping = new RequestMapping();

            if (isStaticPath(httpRequest.getOriginRequestURI())) {
                httpResponse.updatePage(httpRequest.getOriginRequestURI());
                httpResponse.wrapUp(httpRequest.getOriginRequestURI());
                writeMessage(httpResponse, outputStream);
                return;
            }

            final Controller controller = requestMapping.findMappedController(httpRequest.getParsedRequestURI());
            controller.service(httpRequest, httpResponse);
            httpResponse.wrapUp(httpRequest.getParsedRequestURI());
            writeMessage(httpResponse, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeMessage(final HttpResponse httpResponse, final OutputStream outputStream) throws IOException {
        final String message = ResponseParser.parse(httpResponse);
        outputStream.write(message.getBytes());
        outputStream.flush();
    }

    private boolean isStaticPath(final String requestUri) {
        return ContentType.checkFileExtension(requestUri);
    }

    private int getContentLength(final List<String> requestHeaderStrings) {
        final Optional<String> contentLengthHeader = requestHeaderStrings.stream()
                .filter(s -> s.contains("Content-Length"))
                .findFirst();

        if (contentLengthHeader.isEmpty()) {
            return 0;
        }

        final String contentLengthHeaderString = contentLengthHeader.get();
        return Integer.parseInt(contentLengthHeaderString.split(" ")[1]);
    }

    private String parseRequestBodyString(final List<String> requestHeaders, final BufferedReader bufferedReader)
            throws IOException {
        final int contentLength = getContentLength(requestHeaders);
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private List<String> getRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaders = new ArrayList<>();
        String temp;
        while (!Objects.equals(temp = bufferedReader.readLine(), "")) {
            if (temp == null) {
                break;
            }
            requestHeaders.add(temp);
        }
        return requestHeaders;
    }
}
