package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCES_PREFIX = "static";

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
            HttpRequest request = HttpRequest.from(line);
            String path = request.path();

            final var responseBody = getResponseBody(request);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + getExtension(path) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(final HttpRequest request) {
        String path = request.path();

        if (path.equals("/") && request.method().equals("GET")) {
            return "Hello world!";
        }
        if (path.equals("/login") && request.method().equals("GET")) {
            QueryParameters queryParameters = request.queryParameters();
            if (queryParameters.isEmpty()) {
                return modelToView("/login.html");
            }
            User user = queryParameters
                    .get("account")
                    .flatMap(InMemoryUserRepository::findByAccount)
                    .orElse(null);
            String password = queryParameters.get("password").orElse(null);
            if(user == null || !user.checkPassword(password)) {
                return "없는 유저입니다. 다시 입력해주세요";
            }
            log.info(user.toString());
            return modelToView("/login.html");
        }
        return modelToView(path);
    }

    private String getExtension(String path) {
        if (path.endsWith(".html")) {
            return "html";
        }
        if (path.endsWith(".css")) {
            return "css";
        }
        return "html";
    }

    @Nonnull
    private String modelToView(String uri) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + uri);
        if (resource == null) {
            log.info("존재하지 않는 파일 명입니다. 파일 경로를 확인해주세요." + uri);
            return "경로가 잘못됐습니다!!!";
        }
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
