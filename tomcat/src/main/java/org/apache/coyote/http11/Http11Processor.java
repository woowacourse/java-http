package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    private static final String CONTENT_LENGTH = "Content-Length";

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
            Map<String, String> headers = readHeaders(reader);
            String requestBody = null;

            if (headers.containsKey(CONTENT_LENGTH)) {
                int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
            }

            final String response = makeResponse(requestLine, requestBody);

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

    private Map<String, String> readHeaders(BufferedReader reader) {
        Map<String, String> headers = new HashMap<>();

        try {
            while (reader.ready()) {
                String line = reader.readLine();

                if (line.isBlank()) {
                    break;
                }

                String[] entry = line.split(": ");

                if (entry.length == 2) {
                    headers.put(entry[0], entry[1]);
                }
            }
            return headers;
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String makeResponse(String requestLine, String requestBody) {
        String[] params = requestLine.split(" ");
        validateParamCount(params);

        String method = params[0];
        String requestUri = params[1];
        String httpVersion = params[2];
        validateFormat(method, requestUri, httpVersion);

        String endpoint = parseEndpoint(requestUri);
        Map<String, String> queryParams = parseQueryParams(requestUri);
        Map<String, String> requestData = parseRequestBody(requestBody);

        if (method.equals("GET") && endpoint.equals("/")) {
            return makeResponseMessageFromText("Hello world!", HttpStatusCode.OK);
        }

        if (method.equals("POST") && endpoint.equals("/login")) {
            if (requestData.containsKey("account") && requestData.containsKey("password")) {
                String account = requestData.get("account");
                String password = requestData.get("password");

                return InMemoryUserRepository.findByAccountAndPassword(account, password)
                        .map(user -> makeResponseMessageFromFile("index.html", HttpStatusCode.FOUND))
                        .orElseGet(() -> makeResponseMessageFromFile("401.html", HttpStatusCode.UNAUTHORIZED));
            }
        }

        return makeResponseMessageFromFile(endpoint.substring(1), HttpStatusCode.OK);
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

    private String parseEndpoint(String requestUri) {
        return requestUri.split("\\?")[0];
    }

    private Map<String, String> parseQueryParams(String requestUri) {
        String[] substrings = requestUri.split("\\?");

        if (substrings.length == 1) {
            return Collections.emptyMap();
        }

        Map<String, String> queryParams = new HashMap<>();
        String rawQueryParams = requestUri.split("\\?")[1];

        for (String entry : rawQueryParams.split("&")) {
            String[] data = entry.split("=");
            if (data.length != 2) {
                return Collections.emptyMap();
            }
            queryParams.put(data[0], data[1]);
        }

        return queryParams;
    }

    private Map<String, String> parseRequestBody(String requestBody) {
        if (requestBody == null) {
            return Collections.emptyMap();
        }

        Map<String, String> requestData = new HashMap<>();

        for (String entry : requestBody.split("&")) {
            String[] data = entry.split("=");
            if (data.length == 2) {
                requestData.put(data[0], data[1]);
            }
        }

        return requestData;
    }

    private String makeResponseMessageFromText(String content, HttpStatusCode statusCode) {
        return String.join(
                "\r\n",
                "HTTP/1.1 " + statusCode.getValue() + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content
        );
    }

    private String makeResponseMessageFromFile(String fileName, HttpStatusCode statusCode) {
        if (!fileName.contains(".")) {
            fileName += ".html";
        }
        String responseBody = readBody(fileName);
        String contentType = "";

        if (fileName.endsWith(".html")) {
            contentType = "text/html";
        }

        if (fileName.endsWith(".css")) {
            contentType = "text/css";
        }

        if (fileName.endsWith(".svg")) {
            contentType = "image/svg+xml";
        }

        return String.join(
                "\r\n",
                "HTTP/1.1 " + statusCode.getValue() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
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
}
