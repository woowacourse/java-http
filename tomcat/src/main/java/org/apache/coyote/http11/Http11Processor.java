package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
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
        try (final var inputStream = connection.getInputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {
            String requestStartLine = bufferedReader.readLine();
            HttpRequestUrl url = new HttpRequestUrl(requestStartLine.split(" ")[1]);

            String response;
            if (url.equalPath("/")) {
                response = create200Response("Hello world!", ContentType.TEXT_PLAIN);
            } else if (url.equalPath("/login")) {
                String account = url.getParameter("account");
                String password = url.getParameter("password");
                if (account != null && password != null) {
                    validateAccount(account, password);
                }
                response = createStaticResourceResponse("/login.html");
            } else if (url.isStaticResourcePath()) {
                response = createStaticResourceResponse(url.getPath());
            } else {
                response = create404Response();
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createStaticResourceResponse(String path) throws IOException {
        Path staticResource = getStaticResource(path);
        if (staticResource == null) {
            return create404Response();
        }
        String fileExtension = path.split("\\.")[1];
        return create200Response(Files.readString(staticResource), ContentType.of(fileExtension));
    }

    private void validateAccount(String account, String password) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            log.info("존재하지 않는 유저입니다.");
        } else {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                log.info(user.toString());
            } else {
                log.info("비밀번호가 일치하지 않습니다.");
            }
        }
    }

    private Path getStaticResource(String url) {
        URL resourceURL = getClass().getClassLoader().getResource("static" + url);
        if (resourceURL == null) {
            return null;
        }
        return Path.of(resourceURL.getFile());
    }

    private String create200Response(String body, ContentType contentType) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getMimeType() + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private String create404Response() throws IOException {
        String body = Files.readString(getStaticResource("/404.html"));
        return String.join("\r\n",
                "HTTP/1.1 404 NOT FOUND ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
