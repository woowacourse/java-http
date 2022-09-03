package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.Http11Request.Http11Request;
import org.apache.coyote.http11.Http11Request.Http11RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Http11RequestHandler http11RequestHandler;

    public Http11Processor(final Socket connection,
                           final Http11RequestHandler http11RequestHandler) {
        this.connection = connection;
        this.http11RequestHandler = http11RequestHandler;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {
            Http11Request http11Request = http11RequestHandler.makeRequest(bufferedReader);

            final var response = makeResponse(http11Request.getUri());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponse(String fileName) throws IOException {
        String contentType = "text/html";
        if (isCss(fileName)) {
            contentType = "text/css";
        }
        if (hasQueryString(fileName)) {
            Map<String, String> queryStrings = getQueryStrings(fileName.split("\\?")[1]);
            User user = InMemoryUserRepository.findByAccount(queryStrings.get("account"))
                    .orElseThrow(LoginFailedException::new);
            if (!user.checkPassword(queryStrings.get("password"))) {
                throw new LoginFailedException();
            }
            System.out.println(user);
            fileName = fileName.split("\\?")[0];
        }
        System.out.println("fileName: " + fileName);

        String responseBody = getResponseBody(fileName);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private Map<String, String> getQueryStrings(String data) {
        return Arrays.stream(data.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    private boolean hasQueryString(String fileName) {
        return fileName.contains("?");
    }

    private boolean isCss(String fileName) {
        return fileName.contains("/css/");
    }

    private String getResponseBody(String fileName) throws IOException {
        if (fileName.equals("/") || fileName.isEmpty()) {
            return "Hello world!";
        }
        return getContent(fileName);
    }

    private String getContent(String fileName) throws IOException {
        if (!fileName.contains(".")) {
            fileName = fileName + ".html";
        }
        Path path = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + fileName))
                .getFile());
        return Files.readString(path);
    }
}
