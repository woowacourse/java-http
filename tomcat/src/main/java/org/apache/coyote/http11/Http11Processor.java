package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int PATH_INDEX = 1;
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

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
        try (InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = connection.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String requestInfo = br.readLine();
            String[] requestInfoElements = requestInfo.split(" ");
            String requestPath = requestInfoElements[PATH_INDEX];

            String responseBody = extractResponseBody(requestPath);
            String contentType = extractContentType(requestPath);

            if (requestPath.startsWith("/login") && !"/login".equals(requestPath)) {
                if (login(requestPath)) {
                    String response = String.join("\r\n",
                            "HTTP/1.1 302 FOUND ",
                            "Location: /index.html",
                            String.format("Content-Type: %s;charset=utf-8 ", contentType),
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody);

                    outputStream.write(response.getBytes());
                    outputStream.flush();

                    return;
                }
                String response = String.join("\r\n",
                        "HTTP/1.1 302 FOUND ",
                        "Location: /401.html",
                        String.format("Content-Type: %s;charset=utf-8 ", contentType),
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();

                return;
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    String.format("Content-Type: %s;charset=utf-8 ", contentType),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean login(String requestPath) {
        int index = requestPath.indexOf("?");

        if (index == -1) {
            return false;
        }

        Map<String, String> paramMap = handleQueryString(requestPath, index);

        User user = InMemoryUserRepository.findByAccount(paramMap.get(ACCOUNT))
                .orElseThrow(IllegalArgumentException::new);

        if (user.checkPassword(paramMap.get(PASSWORD))) {
            log.info("user : {}", user);

            return true;
        }

        return false;
    }

    private Map<String, String> handleQueryString(String requestPath, int index) {
        String queryString = requestPath.substring(index + 1);
        String[] elements = queryString.split("&");
        return Arrays.stream(elements)
                .map(element -> element.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }

    private String extractResponseBody(String requestPath) throws IOException {
        String nativePath = getNativePath(requestPath);

        if ("/".equals(nativePath)) {
            return "Hello world!";
        }

        if (nativePath.equals("/login")) {
            URL url = getClass().getClassLoader().getResource("static/login.html");

            return new String(Files.readAllBytes(Path.of(url.getPath())));
        }

        URL url = getClass().getClassLoader().getResource("static" + nativePath);

        return new String(Files.readAllBytes(Path.of(url.getPath())));
    }

    private String getNativePath(String requestPath) {
        int index = requestPath.indexOf("?");

        if (index == -1) {
            return requestPath;
        }

        return requestPath.substring(0, index);
    }

    private String extractContentType(String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css";
        }

        if (requestPath.endsWith(".js")) {
            return "text/javascript";
        }

        return "text/html";
    }
}
