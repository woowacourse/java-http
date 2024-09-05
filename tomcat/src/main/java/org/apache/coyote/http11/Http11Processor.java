package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        try (final var inputStream = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            String statusCode = "200 OK";
            String responseBody = "Hello world!";
            String contentType = "text/html;charset=utf-8";
            String uri = "/";
            String redirectUrl = null;
            String requestLine = bufferedReader.readLine();
            if (requestLine != null) {
                String[] requestParts = requestLine.split(" ");
                if (requestParts.length >= 2) {
                    uri = requestParts[1].trim();
                }
            }

            if (uri.equals("/index.html")) {
                final String fileName = "static/index.html";
                responseBody = getResponseBody(fileName);
            } else if (uri.endsWith(".css") || uri.endsWith(".js")) {
                contentType = "text/css";
                if (uri.endsWith(".js")) {
                    contentType = "application/javascript";
                }
                final String fileName = "static" + uri;
                responseBody = getResponseBody(fileName);
            } else if (uri.startsWith("/login")) {
                int parameterStartingIndex = uri.indexOf("?");
                if (parameterStartingIndex > 0) {
                    List<String> queryParameterPairs = Arrays.stream(
                                    uri.substring(parameterStartingIndex + 1).split("&"))
                            .toList();
                    Map<String, String> queryParameters = queryParameterPairs.stream().map(s -> s.split("="))
                            .collect(Collectors.toMap(
                                    keyValue -> keyValue[0],
                                    keyValue -> keyValue[1]
                            ));
                    String account = queryParameters.get("account");
                    String password = queryParameters.get("password");
                    redirectUrl = "/index.html";
                    try {
                        User user = InMemoryUserRepository.findByAccount(account)
                                .orElseThrow(() -> new IllegalArgumentException("account에 해당하는 사용자가 없습니다."));
                        if (!user.checkPassword(password)) {
                            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                        }
                        log.info("user: " + user);
                    } catch (IllegalArgumentException e) {
                        redirectUrl = "/401.html";
                    }
                    statusCode = "302 Found";
                }
                final String fileName = "static/login.html";
                responseBody = getResponseBody(fileName);
            } else if (uri.endsWith("401.html")) {
                final String fileName = "static" + uri;
                responseBody = getResponseBody(fileName);
            }

            List<String> headers = new ArrayList<>();
            headers.add("HTTP/1.1 " + statusCode + " ");
            headers.add("Content-Type: " + contentType + " ");
            headers.add("Content-Length: " + responseBody.getBytes().length + " ");
            if (redirectUrl != null) {
                headers.add("Location: " + redirectUrl);
            }

            final var response = String.join("\r\n", headers) + "\r\n\r\n" + responseBody;

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String fileName) throws IOException {
        final URL url = getClass().getClassLoader().getResource(fileName);
        if (url != null) {
            final File file = new File(url.getFile());
            final Path path = file.toPath();

            final StringBuilder htmlContent = new StringBuilder();
            try (BufferedReader htmlBufferedReader = new BufferedReader(new FileReader(path.toString()))) {
                String line;
                while ((line = htmlBufferedReader.readLine()) != null) {
                    htmlContent.append(line).append("\n");
                }
            }
            return htmlContent.toString();
        }
        return "Hello world!";
    }
}
