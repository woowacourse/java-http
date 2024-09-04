package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final int REQUEST_LINE_PARAM_COUNT = 3;
    private static final Set<String> ALLOWED_METHODS =
            Set.of("GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH");
    private static final String HTTP_1_1 = "HTTP/1.1";
    private static final String STATIC_DIRNAME = "static";
    private static final String NOT_FOUND_FILENAME = "404.html";

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = readRequestLine(reader);
            final String response = makeResponse(requestLine);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedServletException("HTTP 요청의 request line을 읽을 수 없습니다.");
        }
    }

    private String makeResponse(String requestLine) {
        String[] params = requestLine.split(" ");
        validateParamCount(params);

        String method = params[0];
        String requestUri = params[1];
        String httpVersion = params[2];
        validateFormat(method, requestUri, httpVersion);

        if (method.equals("GET") && requestUri.equals("/")) {
            return makeOkResponseMessage("Hello world!");
        }

        return makeOkResponseMessage(readBody(requestUri.substring(1)));
    }

    private static void validateParamCount(String[] params) {
        if (params.length != REQUEST_LINE_PARAM_COUNT) {
            throw new UncheckedServletException(
                    String.format(
                            "Request line의 인자는 %d개여야 합니다. 입력된 인자 개수 = %d",
                            REQUEST_LINE_PARAM_COUNT,
                            params.length
                    )
            );
        }
    }

    private void validateFormat(String method, String requestUri, String httpVersion) {
        if (!ALLOWED_METHODS.contains(method)) {
            throw new UncheckedServletException(String.format("허용되지 않는 HTTP method입니다. 입력값 = %s", method));
        }
        if (!requestUri.startsWith("/")) {
            throw new UncheckedServletException(String.format("Request URI는 /로 시작하여야 합니다. 입력값 = %s", requestUri));
        }
        if (!httpVersion.equals(HTTP_1_1)) {
            throw new UncheckedServletException(String.format("HTTP 버전은 %s만 허용됩니다. 입력값 = %s", HTTP_1_1, httpVersion));
        }
    }

    private String readBody(String fileName) {
        try {
            URI uri = getClass().getClassLoader().getResource(STATIC_DIRNAME + "/" + fileName).toURI();
            Path path = Paths.get(uri);
            return Files.readString(path);
        } catch (NullPointerException e) {
            return readBody(NOT_FOUND_FILENAME);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
    }

    private String makeOkResponseMessage(String responseBody) {
        return String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }
}
