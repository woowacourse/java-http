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

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    String.format("Content-Type: %s;charset=utf-8 ", contentType),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            if (requestPath.startsWith("/login")) {
                handleLoginQueryString(requestPath);

            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleLoginQueryString(String requestPath) {
        int index = requestPath.indexOf("?");

        if (index == -1) {
            return;
        }

        String queryString = requestPath.substring(index + 1);
        String[] elements = queryString.split("&");
        Map<String, String> paramMap = Arrays.stream(elements)
                .map(element -> element.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));

        User user = InMemoryUserRepository.findByAccount(paramMap.get(ACCOUNT))
                .orElseThrow(IllegalArgumentException::new);

        if (user.checkPassword(paramMap.get(PASSWORD))) {
            log.info("user : {}",user);
        }
    }

    private String extractResponseBody(String requestPath) throws IOException {
        if ("/".equals(requestPath)) {

            return "Hello world!";
        }

        if (requestPath.startsWith("/login")) {
            URL url = getClass().getClassLoader().getResource("static/login.html");

            return new String(Files.readAllBytes(Path.of(url.getPath())));
        }

        URL url = getClass().getClassLoader().getResource("static" + requestPath);

        return new String(Files.readAllBytes(Path.of(url.getPath())));
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
