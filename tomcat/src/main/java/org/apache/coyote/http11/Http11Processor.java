package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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

            String responseBody = "Hello world!";
            String request = getRequest(inputStream);
            String[] split = request.split(" ");
            String requestUri = split[1];

            if (requestUri.startsWith("/login?")) {
                login(requestUri, outputStream);
                return;
            }

            if (!requestUri.equals("/")) {
                URL resource = getClass().getClassLoader().getResource("static" + requestUri);
                if (requestUri.equals("/login")) {
                    resource = getClass().getClassLoader().getResource("static" + requestUri + ".html");
                }
                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            if (request.contains("Accept: text/css")) {
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void login(String requestUri, OutputStream outputStream) throws IOException {
        int index = requestUri.indexOf("?");
        if (index == -1) {
            throw new IllegalArgumentException("로그인 정보가 없습니다");
        }
        String path = requestUri.substring(0, index);
        String queryString = requestUri.substring(index + 1);
        String[] keyValues = queryString.split("&");
        Map<String, String> queryParams = new HashMap<>();
        for (String keyValue : keyValues) {
            String[] keyAndValue = keyValue.split("=");
            String key = keyAndValue[0];
            String value = keyAndValue[1];
            queryParams.put(key, value);
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.get("account"));
        if (user.isPresent()) {
            if (user.get().checkPassword(queryParams.get("password"))) {
                log.info(user.get().toString());
                String response = String.join("\r\n",
                        "HTTP/1.1 302 FOUND ",
                        "Location: /index.html ",
                        "",
                        "");
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
        }
        URL resource = getClass().getClassLoader().getResource("static/401.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String response = String.join("\r\n",
                "HTTP/1.1 401 UNAUTHORIZED ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String getRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (!"".equals(line)) {
            stringBuilder.append(line);
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }
}
