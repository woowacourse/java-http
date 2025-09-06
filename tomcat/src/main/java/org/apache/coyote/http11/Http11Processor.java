package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
                final var outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();
            String requestUri = line.split(" ")[1];
            if (requestUri.contains("?")) {
                requestUri = queryParameterProcess(requestUri);
            }

            byte[] bytes = calculateBytes(requestUri);

            final String responseHeader = getResponseHeader(requestUri, bytes);

            outputStream.write(responseHeader.getBytes());
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] calculateBytes(String requestUri) throws IOException {
        if (requestUri.equals("/")) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }
        URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        if (resource == null) {
            resource = getClass().getClassLoader().getResource("static/404.html");
        }
        return Files.readAllBytes(Path.of(resource.getFile()));
    }

    private String queryParameterProcess(String requestUri) {
        if (requestUri.contains("/login")) {
            loginProcess(requestUri);
        }
        int index = requestUri.indexOf("?");
        requestUri = requestUri.substring(0, index) + ".html";
        return requestUri;
    }

    private void loginProcess(final String requestUri) {
        Map<String, String> queryParams = new HashMap<>();
        String queryString = requestUri.substring(requestUri.indexOf("?") + 1);
        /**
         * split[0] = account
         * split[1] = accountId
         * split[2] = password
         * split[3] = password value
         */
        String[] split = queryString.split("[=&]");
        queryParams.put(split[0], split[1]);
        queryParams.put(split[2], split[3]);
        loggingLoginUserInfo(queryParams);
    }

    private void loggingLoginUserInfo(final Map<String, String> queryParams) {
        try {
            final User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                    .orElseThrow(() -> new IllegalArgumentException("이런 유저는 없답니다."));
            if (user.checkPassword(queryParams.get("password"))) {
                log.info(user.toString());
            }
        } catch (IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    private String getResponseHeader(final String requestUri, final byte[] bytes) {
        String responseHeader = "";
        if (requestUri.endsWith(".css")) {
            responseHeader = getHeader(bytes, "css");
        }
        if (requestUri.endsWith(".html") || requestUri.equals("/")) {
            responseHeader = getHeader(bytes, "html");
        }

        if (requestUri.endsWith(".js")) {
            responseHeader = getHeader(bytes, "js");
        }
        return responseHeader;
    }

    private String getHeader(final byte[] bytes, String type) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                ""
        ) + "\r\n";
    }
}
