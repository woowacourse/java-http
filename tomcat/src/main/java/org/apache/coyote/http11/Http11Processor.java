package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
             final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)))
        {
            final String line = br.readLine(); //GET /index.html HTTP/1.1
            String uri = parseUri(line); //index.html, /login?...

            String path = extractPath(uri); //index.html, /login
            String query = extractQuery(uri);
            String resourcePath;

            if (path.equals("/login")) {
                resourcePath = "static/login.html"; // 항상 .html 붙이기
            }
            else {
                resourcePath = "static" + path; // 그 외 정적 파일
            }

            URL url = getClass().getClassLoader().getResource(resourcePath);
            if (url == null) {
                url = getClass().getClassLoader().getResource("static/404.html");
            }

            String responseBody;
            if (uri.equals("/")) {
                responseBody = "Hello world!";
            }
            else if (path.equals("/login") && url != null) {
                Path loginPath = Paths.get(url.toURI());
                responseBody = Files.readString(loginPath);
                parseQueryString(query);
            }
            else if (path.equals("/index.html") && url != null) {
                Path rootPath = Paths.get(url.toURI());
                responseBody = Files.readString(rootPath);
            }
            else {
                Path notFoundPath = Paths.get(url.toURI());
                responseBody = Files.readString(notFoundPath);
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(path),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void parseQueryString(final String query) {
        //user : USER{id=1, account='gugu', email='dfad@gmail.com', password='password'}
        if (query == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");

        for(String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }

        String account = params.get("account");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        user -> log.info("user : {}", user),
                        () -> log.warn("account에 일치하는 user가 존재하지 않습니다.")
                );
    }

    private String parseUri(final String requestLine) {
        String[] requestValues = requestLine.split(" ");

        return requestValues[1]; // /index.html or /login?account=...
    }

    private String extractPath(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }

        return uri.substring(0, index);
    }

    private String extractQuery(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return null;
        }

        return uri.substring(index + 1);
    }

    private String getContentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }

        return "text/html;charset=utf-8 ";
    }
}
