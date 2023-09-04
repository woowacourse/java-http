package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
             final var outputStream = connection.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String firstLine = reader.readLine();
            if (firstLine == null) {
                return;
            }
            final StringBuilder requestHeader = new StringBuilder(firstLine);

            String line;
            while (!Objects.equals(line = reader.readLine(), "")) {
                requestHeader.append(line).append("\r\n");
            }

            StringBuilder requestBody = new StringBuilder();
            int contentLength = getContentLength(requestHeader.toString());

            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                int bytesRead = reader.read(buffer, 0, contentLength);
                if (bytesRead > 0) {
                    requestBody.append(buffer, 0, bytesRead);
                }
            }

            String uri = extractRequestUriFromRequest(requestHeader);

            int index = uri.indexOf("?");
            String path;
            String queryString = "";

            int statusCode = 200;
            String statusMessage = "OK";

            if (index != -1) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            } else {
                path = uri;
            }

            System.out.println("path = " + path);

            if (path.equals("login") && !queryString.equals("")) {
                statusCode = 302;
                statusMessage = "Found";

                Map<String, String> queryParams = parseQueryParams(queryString);
                String account = queryParams.get("account");
                String password = queryParams.get("password");

                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                if (user.isPresent()) {
                    log.info(user.toString());

                    path = "/index.html";
                } else {
                    log.warn("미가입회원입니다");

                    path = "/401.html";
                }
            }

            Map<String, String> requestBodyMap = parseQueryParams(requestBody.toString());

            if (path.equals("register") && requestHeader.indexOf("POST") != -1) {
                String account = requestBodyMap.get("account");
                String password = requestBodyMap.get("password");
                String email = requestBodyMap.get("email");

                InMemoryUserRepository.save(new User(account, password, email));
                path = "/index.html";
            }

            String contentType = "html";
            if (path.length() != 0) {
                String[] splitedFileName = path.split("\\.");
                if (splitedFileName.length != 1) {
                    contentType = splitedFileName[1];
                }
                if (splitedFileName.length == 1) {
                    path += ".html";
                }
            }

            var responseBody = "";

            if (uri.length() == 0) {
                responseBody = "Hello world!";
            } else {
                final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static/" + path)).getPath());

                responseBody = new String(Files.readAllBytes(filePath));
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
                    "Content-Type: text/" + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestUriFromRequest(StringBuilder request) {
        int startIndex = 0;
        if (request.indexOf("GET") != -1) {
            startIndex = request.indexOf("GET ") + 4;
        }
        if (request.indexOf("POST") != -1) {
            startIndex = request.indexOf("POST ") + 5;
        }
        int endIndex = request.indexOf(" HTTP/1.1");

        if (startIndex != -1 && endIndex != -1) {
            return request.substring(startIndex + 1, endIndex);
        }

        return "";
    }

    private Map<String, String> parseQueryParams(String input) {
        Map<String, String> params = new HashMap<>();

        String[] keyValuePairs = input.split("&");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                params.put(key, value);
            }
        }

        return params;
    }

    private int getContentLength(String requestHeader) {
        String[] headerLines = requestHeader.split("\r\n");
        for (String line : headerLines) {
            if (line.startsWith("Content-Length:")) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    try {
                        return Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        return 0;
    }
}
