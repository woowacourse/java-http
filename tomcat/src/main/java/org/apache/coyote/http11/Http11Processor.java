package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String request = reader.readLine();
            if (request == null) {
                return;
            }
            String requestUri = request.split(" ")[1];

            String line = reader.readLine();

            while (!"".equals(line)) {
                if (line == null) {
                    return;
                }

                line = reader.readLine();
            }

            String queryString = "";
            int index = requestUri.indexOf("?");
            if (index != -1) {
                queryString = requestUri.substring(index + 1);
                requestUri = requestUri.substring(0, index);
            }

            if (requestUri.equals("/login")) {
                Map<String, String> params = new HashMap<>();
                for (String param : queryString.split("&")) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        params.put(keyValue[0], keyValue[1]);
                    }
                }

                InMemoryUserRepository.findByAccount(params.getOrDefault("account", ""))
                        .filter(user -> user.checkPassword(params.get("password")))
                        .ifPresent(user -> log.info("user : {}", user));

                requestUri = "/login.html";
            }

            var responseBody = "Hello world!";

            var contentType = "text/html";

            if (!requestUri.equals("/")) {
                //클래스는 클래스로더에 대한 정보를 가짐
                //클래스로더는 파일의 위치에 대한 정보를 가짐
                //getResource는 파일을 찾지 못하면 null을 반환함
                URL resource = getClass().getClassLoader().getResource("static" + requestUri);

                if (requestUri.endsWith(".css")) {
                    contentType = "text/css";
                }

                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: "+ contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
