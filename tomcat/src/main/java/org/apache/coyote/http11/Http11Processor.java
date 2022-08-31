package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            String responseBody;
            String contentType;

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String url = Objects.requireNonNull(bufferedReader.readLine()).split(" ")[1];
            if (url.equals("/")) {
                responseBody = "Hello world!";
                contentType = ContentType.HTML.getValue();

            } else if (url.startsWith("/login?")) {
                final String[] parameters = url.split("[?&=]");
                checkLoginParameters(parameters);
                final User user = InMemoryUserRepository.findByAccount(parameters[2])
                        .orElseThrow(() -> new IllegalArgumentException("잘못된 account 입니다."));
                if (!user.checkPassword(parameters[4])) {
                    throw new IllegalArgumentException("잘못된 password 입니다.");
                }
                responseBody = getResponseBody("/login.html");
                contentType = ContentType.HTML.getValue();
                System.out.println(user);

            } else {
                responseBody = getResponseBody(url);
                contentType = ContentType.of(url.split("\\.")[1]);
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkLoginParameters(String[] parameters) {
        checkAccount(parameters[1]);
        checkPassword(parameters[3]);
    }

    private void checkAccount(String parameter) {
        if (!"account".equals(parameter)) {
            throw new IllegalArgumentException("account 요청 형식이 잘못되었습니다.");
        }
    }

    private void checkPassword(String parameter) {
        if (!"password".equals(parameter)) {
            throw new IllegalArgumentException("password 요청 형식이 잘못되었습니다.");
        }
    }

    private String getResponseBody(String url) throws IOException {
        String responseBody;
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        final String file = Objects.requireNonNull(resource).getFile();
        responseBody = new String(Files.readAllBytes(new File(file).toPath()));
        return responseBody;
    }
}
