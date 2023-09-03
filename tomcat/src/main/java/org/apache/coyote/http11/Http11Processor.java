package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            String body = createBody(line);
            String header = createHeader(line, body);

            outputStream.write((header + body).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createBody(String request) throws IOException {
        String uri = "";

        for (String element : request.split(" ")) {
            if (element.startsWith("/")) {
                uri = element;
            }
        }

        int index = uri.indexOf("?");
        String path = uri;
        String query = "";
        if (index != -1) {
            path = uri.substring(0, index);
            query = uri.substring(index + 1);
        }

        if (Objects.equals(path, "/")) {
            return "Hello world!";
        }

        if (Objects.equals(path, "/login")) {
            createLogin(query);
        }

        if (!path.contains(".")) {
            path += ".html";
        }

        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        final URL resource = systemClassLoader.getResource("static" + path);
        String resourcePath = resource.getPath();
        if (request == null) {
            return "Not Found";
        }
        File file = new File(resourcePath);
        return new String(Files.readAllBytes(file.toPath()));
    }

    private void createLogin(String query) {
        if (query.isBlank()) {
            return;
        }

        String account = "";
        String password = "";

        for (String parameter : query.split("&")) {
            int idx = parameter.indexOf("=");
            String key = parameter.substring(0, idx);
            String value = parameter.substring(idx + 1);
            if ("account".equals(key)) {
                account = value;
            }
            if ("password".equals(key)) {
                password = value;
            }
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        if (user.checkPassword(password)) {
            System.out.println(user);
        }
    }

    private String createHeader(String request, String body) {

        StringJoiner stringJoiner = new StringJoiner("\r\n");
        String contentType = "text/html;charset=utf-8";

        if (request.contains(".css")) {
            contentType = "text/css;charset=utf-8";
        }

        stringJoiner.add("HTTP/1.1 200 OK ");
        stringJoiner.add("Content-Type: " + contentType + " ");
        stringJoiner.add("Content-Length: " + body.getBytes().length + " ");
        stringJoiner.add("\r\n");
        return stringJoiner.toString();
    }
}
