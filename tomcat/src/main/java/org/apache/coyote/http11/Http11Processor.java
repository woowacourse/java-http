package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.router.FrontController;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final FrontController frontController;

    public Http11Processor(final Socket connection, final FrontController frontController) {
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest request = parseRequest(bufferedReader);
            final HttpResponse response = new HttpResponse();

            frontController.dispatch(request, response);

            sendResponse(response, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final BufferedReader bufferedReader) throws IOException {
        final String[] request = bufferedReader.readLine().split(" ");
        final RequestLine requestLine = new RequestLine(HttpMethod.valueOf(request[0]), request[1], request[2]);
        final List<String> lines = getHeaders(bufferedReader);
        final Map<String, List<String>> headers = parseHeaders(lines);
        final HttpHeaders httpHeaders = new HttpHeaders(headers);
        final int contentLength = getContentLengthFromHeaders(lines);
        final byte[] body = readRequestBody(bufferedReader, contentLength);

        return new HttpRequest(requestLine, httpHeaders, body);
    }

    private List<String> getHeaders(final BufferedReader reader) throws IOException {
        return reader.lines()
                .takeWhile(line -> !line.isBlank())
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> parseHeaders(List<String> lines) {
        return lines.stream()
                .map(line -> line.split(":", 2))
                .collect(Collectors.groupingBy(
                        arr -> arr[0].trim(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                arr -> arr.length > 1 ? arr[1].trim() : "",
                                Collectors.toList()
                        )
                ));
    }

    private int getContentLengthFromHeaders(final List<String> headers) {
        return headers.stream()
                .filter(h -> h.startsWith("Content-Length"))
                .map(h -> h.split(":")[1].trim())
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);
    }

    private byte[] readRequestBody(final BufferedReader reader, final int contentLength) throws IOException {
        if (contentLength <= 0) {
            return new byte[0];
        }

        char[] bodyChars = new char[contentLength];
        int read = reader.read(bodyChars, 0, contentLength);

        if (read == -1) {
            return new byte[0];
        }

        return new String(bodyChars, 0, read).getBytes(StandardCharsets.UTF_8);
    }

    private void sendResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
