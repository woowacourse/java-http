package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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

            final var reader = new BufferedReader(
                    new InputStreamReader(
                            inputStream,
                            StandardCharsets.UTF_8
                    )
            );

            // 1. Request Line
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            // GET /login?account=gugu&password=password HTTP/1.1
            final String[] requestLineParts = requestLine.split(" ");
            final String uri = requestLineParts[1];

            // 2. path와 query string 분리
            String path = uri;
            String queryString = null;

            final int queryIndex = uri.indexOf("?");

            if (queryIndex != -1) {
                path = uri.substring(0, queryIndex);
                queryString = uri.substring(queryIndex + 1);
            }

            // 3. 로그인 요청 + Query String이 있으면 회원 조회
            if ("/login".equals(path) && queryString != null) {
                final Map<String, String> params = new HashMap<>();

                for (String parameter : queryString.split("&")) {
                    final String[] pair = parameter.split("=", 2);

                    if (pair.length == 2) {
                        params.put(pair[0], pair[1]);
                    }
                }

                final String account = params.get("account");
                final String password = params.get("password");

                if (account != null && password != null) {
                    final var user =
                            InMemoryUserRepository.findByAccount(account);

                    if (user.isPresent()
                            && user.get().checkPassword(password)) {

                        log.info("user: {}", user.get());
                    }
                }
            }

            // 4. Response Body 결정
            String responseBody;
            String contentType;

            if ("/".equals(path)) {
                responseBody = "Hello world!";
                contentType = "text/html;charset=utf-8";

            } else {
                final String resourcePath;

                if ("/login".equals(path)) {
                    resourcePath = "static/login.html";
                } else {
                    resourcePath = "static" + path;
                }

                final URL resource = getClass()
                        .getClassLoader()
                        .getResource(resourcePath);

                final Path resourceFile = Path.of(resource.toURI());

                responseBody = Files.readString(
                        resourceFile,
                        StandardCharsets.UTF_8
                );

                if (path.endsWith(".css")) {
                    contentType = "text/css";
                } else if (path.endsWith(".js")) {
                    contentType = "application/javascript";
                } else {
                    contentType = "text/html;charset=utf-8";
                }
            }

            // 5. HTTP Response 생성
            final byte[] responseBodyBytes =
                    responseBody.getBytes(StandardCharsets.UTF_8);

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBodyBytes.length + " ",
                    "",
                    responseBody
            );

            // 6. 전송
            outputStream.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );

            outputStream.flush();

        } catch (IOException
                 | URISyntaxException
                 | UncheckedServletException e) {

            log.error(e.getMessage(), e);
        }
    }
}