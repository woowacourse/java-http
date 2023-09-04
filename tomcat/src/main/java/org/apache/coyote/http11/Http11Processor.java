package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
             final var outputStream = connection.getOutputStream()) {
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }

            final Map<String, String> headers = parseHeader(bufferedReader);

            String response = null;
            final String[] parsedFirstLine = firstLine.split(" ");

            response = parseStaticFiles(parsedFirstLine, response);
            if (parsedFirstLine[1].contains("login")) {
                response = parseLoginPage(parsedFirstLine, response);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseHeader(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String header = bufferedReader.readLine();
        while (!"".equals(header)) {
            final String[] parsedHeader = header.split(": ");
            headers.put(parsedHeader[0], parsedHeader[1]);
            header = bufferedReader.readLine();
        }

        return headers;
    }

    private String parseStaticFiles(final String[] parsedFirstLine, String response) throws IOException {
        if ("/".equals(parsedFirstLine[1])) {
            final var responseBody = "Hello world!";

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if ("/index.html".equals(parsedFirstLine[1])) {
            final String filePath = "static" + parsedFirstLine[1];
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (parsedFirstLine[1].endsWith(".js")) {
            final String filePath = "static" + parsedFirstLine[1];
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/javascript ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (parsedFirstLine[1].endsWith(".css")) {
            final String filePath = "static" + parsedFirstLine[1];
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (parsedFirstLine[1].endsWith(".ico")) {
            final String filePath = "static" + parsedFirstLine[1];
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: image/x-icon ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        return null;
    }

    private String parseLoginPage(final String[] parsedFirstLine, String response) throws IOException {
        final String requestUri = parsedFirstLine[1];

        if (requestUri.contains("login")) {
            final String[] parsedRequestUri = requestUri.split("\\?");

            if (requestUri.contains("?")) {

                final String uri = parsedRequestUri[0];
                final Map<String, String> queryStrings = Arrays.stream(parsedRequestUri[1].split("&"))
                        .map(param -> param.split("="))
                        .collect(Collectors.toMap(param -> param[0], param -> param[1]));

                final String account = queryStrings.get("account");
                final String password = queryStrings.get("password");

                try {
                    final User user = InMemoryUserRepository.findByAccount(account)
                            .orElseThrow(() -> new IllegalArgumentException("잘못된 계정입니다. 다시 입력해주세요."));

                    if (!user.checkPassword(password)) {
                        throw new IllegalArgumentException("잘못된 비밀번호입니다. 다시 입력해주세요.");
                    }
                    log.info("로그인 성공! user = {}", user);
                } catch (final IllegalArgumentException e) {
                    log.warn("login error = {}", e);
                    return String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: /401.html ");
                }


                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /index.html ");
            }

            final String filePath = "static/login.html";
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

        }

        return null;
    }
}
