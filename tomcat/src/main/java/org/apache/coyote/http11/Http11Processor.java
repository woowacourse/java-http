package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.controller.BasicController;
import org.apache.controller.Controller;
import org.apache.controller.LoginController;
import org.apache.controller.RegisterController;
import org.apache.controller.StaticController;
import org.apache.coyote.Processor;
import org.apache.http.HttpCookie;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private static final List<Controller> controllers = List.of(
            new BasicController(),
            new LoginController(),
            new RegisterController(),
            new StaticController()
    );

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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final Map<String, String> requests = parseRequest(bufferedReader);

            final String path = requests.get("Path");

            Controller processorableController = controllers.stream()
                    .filter(controller -> controller.isProcessable(path))
                    .findFirst()
                    .orElseThrow(() -> new IOException("처리할 수 있는 컨트롤러가 없습니다."));

            Map<String, Object> responseBody = processorableController.process(requests);

            HttpCookie cookieToSet = (HttpCookie) responseBody.getOrDefault("cookie", null);

            String response = makeResponse(
                    String.valueOf(responseBody.get("responseBody")),
                    requests,
                    (HttpStatus) responseBody.get("status"),
                    cookieToSet
            );

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponse(String responseBody,
                                Map<String, String> requests,
                                HttpStatus httpStatus,
                                HttpCookie httpCookie) {

        final String contentType = parseContentType(requests.getOrDefault("Accept", ""));
        final String protocol = requests.getOrDefault("Protocol", "");

        final String statusLine = protocol + " " + httpStatus.getCode() + " " + httpStatus.getCodeName() + " ";

        StringBuilder headers = new StringBuilder();
        headers.append("Content-Type: ").append(contentType).append(" \r\n");
        headers.append("Content-Length: ").append(responseBody.getBytes().length).append(" \r\n");

        if (httpCookie != null) {
            headers.append("Set-Cookie: ").append(httpCookie.getKeyAndJSessionID()).append("\r\n");
        }

        if (httpStatus == HttpStatus.FOUND) {
            headers.append("Location: /index.html\r\n");
        }

        return String.join("\r\n",
                statusLine,
                headers.toString(),
                responseBody);
    }

    private Map<String, String> parseRequest(BufferedReader reader) throws IOException {
        Map<String, String> requestKeyAndValue = new HashMap<>();

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("유효하지 않은 요청입니다.");
        }
        parseRequestLine(requestLine, requestKeyAndValue);

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            parseHeader(headerLine, requestKeyAndValue);
        }

        if (requestKeyAndValue.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(requestKeyAndValue.get("Content-Length"));
            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);
                parseBody(requestBody, requestKeyAndValue);
            }
        }

        return requestKeyAndValue;
    }

    private void parseRequestLine(String requestLine, Map<String, String> requestKeyAndValue) {
        String[] parts = requestLine.split(" ");
        if (parts.length >= 2) {
            requestKeyAndValue.put("Method", parts[0]);
            requestKeyAndValue.put("Path", parts[1]);
            if (parts.length >= 3) {
                requestKeyAndValue.put("Protocol", parts[2]);
            }
        }
    }

    private void parseHeader(String headerLine, Map<String, String> requestKeyAndValue) {
        final int separatorIndex = headerLine.indexOf(":");
        if (separatorIndex != -1) {
            String key = headerLine.substring(0, separatorIndex).trim();
            String value = headerLine.substring(separatorIndex + 1).trim();
            requestKeyAndValue.put(key, value);
        }
    }

    private void parseBody(String requestBody, Map<String, String> requestKeyAndValue) {
        String[] requests = requestBody.split("&");

        for (String request : requests) {
            String[] requestParts = request.split("=");
            requestKeyAndValue.put(requestParts[0], requestParts[1]);
        }
    }

    private String parseContentType(String headerAccept) {
        if (headerAccept.isBlank()) {
            return DEFAULT_CONTENT_TYPE;
        }
        return headerAccept.split(",")[0];
    }
}
