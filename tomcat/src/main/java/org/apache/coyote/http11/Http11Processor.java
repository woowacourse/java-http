package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.common.Content;
import org.apache.common.HttpStatus;
import org.apache.coyote.Processor;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_PATH = "/";
    private static final String DEFAULT_RESPONSE = "Hello world!";
    private static final String DEFAULT_RESOURCE_LOCATION = "static";

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            HttpRequest httpRequest = HttpRequest.of(bufferedReader);

            String url = httpRequest.getTarget();
            Content content = readContent(url);
            String response = HttpResponse.create(content.getValue(), url, content.getHttpStatus());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Content readContent(String path) throws IOException {
        if (Objects.equals(path, DEFAULT_PATH)) {
            return new Content(DEFAULT_RESPONSE, HttpStatus.OK);
        }

        if (Objects.equals(path, "/favicon.ico")) {
            return new Content(Files.readAllBytes(Paths.get(convertPathToUri("/favicon.ico"))).toString(), HttpStatus.OK);
        }

        if (path.startsWith("/login")) {
            int index = path.indexOf("?");
            if (index > 0) {
                String query = path.substring(index + 1);
                String[] queries = query.split("&");
                String account = queries[0].split("=")[1];
                String password = queries[1].split("=")[1];

                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                if (user.isPresent() && user.get().checkPassword(password)) {
                    log.info("로그인 성공한 회원 : {}", user);
                    return new Content(Files.readString(Paths.get(convertPathToUri("/index.html"))),  HttpStatus.FOUND);
                }
                return new Content(Files.readString(Paths.get(convertPathToUri("/401.html"))), HttpStatus.UNAUTHORIZED);
            }
            path = "/login.html";
        }

        if (Objects.equals(path, "/register")) {
            path = "/register.html";
        }

        URI uri = convertPathToUri(path);
        
        return new Content(Files.readString(Paths.get(uri)), HttpStatus.OK);
    }

    private URI convertPathToUri(String path) {
        URL url = getClass().getClassLoader().getResource(DEFAULT_RESOURCE_LOCATION + path);
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
