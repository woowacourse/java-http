package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();

            if (line == null) {
                return;
            }

            String[] requestLine = line.split(" ");

            String method = requestLine[0];
            String uri = requestLine[1];
            String version = requestLine[2];

            String path = uri;
            String queryString = null;

            int queryIndex = uri.indexOf("?");

            if (queryIndex != -1) {
                path = uri.substring(0, queryIndex);
                queryString = uri.substring(queryIndex + 1);
            }
            path = normalizePath(path);

            String header;

            while ((header = reader.readLine()) != null && !header.isEmpty()) {
                // Header는 현재 사용하지 않으므로 읽고 버린다.
            }

            if (queryString != null) {
                Map<String, String> queryParams = parseQueryString(queryString);

                String account = queryParams.get("account");

                InMemoryUserRepository.findByAccount(account) // 추후 UserService 생성
                        .ifPresent(user ->
                                log.info("조회된 사용자: id={}, account={}",
                                        user.getId(),
                                        user.getAccount()
                                )
                        );
            }

            // 추후 requestLine(method/path) 검증 추가 예정

            URL resource = getClass()
                    .getClassLoader()
                    .getResource("static" + path);

            if (resource == null) {
                return;
            }

            byte[] body;
            try (InputStream resourceStream = resource.openStream()) {
                body = resourceStream.readAllBytes();
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(path) + " ",
                    "Content-Length: " + body.length + " ",
                    "",
                    ""
            );

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.write(body);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private String normalizePath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }

        if (!path.contains(".")) {
            return path + ".html";
        }

        return path;
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();

        for (String parameter : queryString.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            String key = keyValue[0];
            String value = keyValue[1];

            queryParams.put(key, value);
        }

        return queryParams;
    }
}
