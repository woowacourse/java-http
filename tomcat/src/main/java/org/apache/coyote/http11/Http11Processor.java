package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.techcourse.application.UserService;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            String line = readHeaders(inputStream);
            String uri = line.split(" ")[1];
            String queryString = "";
            int queryIndex = uri.indexOf("?");
            if (queryIndex != -1) {
                queryString = uri.substring(queryIndex + 1);
                uri = uri.substring(0, queryIndex);
            }
            String fileExtension = "";

            int index = uri.lastIndexOf(".");
            String fileName = uri;
            if (index == -1) {
                fileExtension = "html";
            } else {
                fileName = uri.substring(0, index);
                fileExtension = uri.substring(index + 1);
            }

            // URI가 '/'일 때 "hello world" 응답 추가
            String responseBody;
            String contentType;

            if ("/".equals(uri)) {
                responseBody = "Hello world!";
                contentType = "text/html;charset=utf-8";
            } else {
                Map<String, String> params = parseQueryString(queryString);
                URL resource = getClass().getClassLoader().getResource("static" + fileName + "." + fileExtension);
                if (resource == null) {
                    // 리소스가 없을 경우 404 처리
                    responseBody = "404 Not Found";
                    contentType = "text/plain;charset=utf-8";
                } else {
                    Path path = Paths.get(resource.getPath());
                    responseBody = new String(Files.readAllBytes(path));
                    contentType = createContentType(fileExtension);
                }

                // 로그인 처리
                if (uri.startsWith("/login")) {
                    String account = params.get("account");
                    String password = params.get("password");
                    User user = new UserService().login(account, password);
                    log.debug("user: {}", user);
                }
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }


    private String readHeaders(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.readLine();
    }

    private String createContentType(String fileExtension) {
        String contentType;
        if (fileExtension.endsWith("html")) {
            contentType = "text/html;charset=utf-8";
        } else if (fileExtension.endsWith("css")) {
            contentType = "text/css;charset=utf-8";
        } else {
            contentType = "text/plain;charset=utf-8";
        }
        return contentType;
    }

    private Map<String, String> parseQueryString(String queryString) {
        if (queryString.isEmpty()) {
            return new HashMap<>();
        }
        return Arrays.stream(queryString.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }
}
