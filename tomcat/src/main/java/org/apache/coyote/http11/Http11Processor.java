package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_FILE_PREFIX = "static";

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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine = bufferedReader.readLine();
            if (inputLine == null || inputLine.isEmpty()) {
                return;
            }

            String httpMethod = inputLine.split(" ")[0];
            String httpUri = inputLine.split(" ")[1];

            String response = null;
            if (httpUri.startsWith("/login")) {
                int index = httpUri.indexOf("?");
                String uri = httpUri.substring(0, index);
                String query = httpUri.substring(index + 1);

                Map<String, String> tmp = new HashMap<>();

                String[] queryStrings = query.split("&");
                for (String keyValue : queryStrings) {
                    String[] keyValues = keyValue.split("=");
                    tmp.put(keyValues[0], keyValues[1]);
                }
                String account = tmp.get("account");

                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                log.info("{}", user.orElse(null));

                response = getHttpResponse(
                    "html",
                    new String(Files.readAllBytes(new File(ClassLoader.getSystemResource(STATIC_FILE_PREFIX + uri + ".html").getFile()).toPath()))
                );
            } else if (httpUri.contains(".")) {
                String staticFile = new String(Files.readAllBytes(new File(ClassLoader.getSystemResource(STATIC_FILE_PREFIX + httpUri).getFile()).toPath()));
                response = getHttpResponse(httpUri.substring(httpUri.indexOf(".") + 1), staticFile);
            } else {
                response = getHttpResponse("html", "Hello world!");
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getHttpResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/" + contentType + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
