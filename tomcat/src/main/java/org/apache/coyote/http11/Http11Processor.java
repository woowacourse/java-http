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
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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
            String content = readContent(url);
            String response = HttpResponse.create(content, url);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readContent(String path) throws IOException {
        System.out.println("path = " + path);
        if (Objects.equals(path, DEFAULT_PATH)) {
            return DEFAULT_RESPONSE;
        }

        if (path.startsWith("/login")) {
            int index = path.indexOf("?");
            if (index > 0) {
                String query = path.substring(index + 1);
                String[] queries = query.split("&");
                String account = queries[0].split("=")[1];
                String password = queries[1].split("=")[1];

                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("일치하는 회원을 찾을 수 없습니다."));

                if (user.checkPassword(password)) {
                    log.info("로그인 성공한 회원 : {}", user);
                }
            }
            path = "/login.html";
        }

        if (Objects.equals(path, "/register")) {
            path = "/register.html";
        }

        URI uri = convertPathToUri(path);
        
        return Files.readString(Paths.get(uri));
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
