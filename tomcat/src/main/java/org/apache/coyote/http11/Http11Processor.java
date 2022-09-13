package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final HttpRequest httpRequest = HttpRequest.of(bufferedReader.readLine(), getHeaderLines(bufferedReader));

            processLogin(httpRequest.getRequestURL());
            final String contentType = checkContentType(httpRequest.getRequestURL());
            final String responseBody = getResponseBody(httpRequest);

            final var response = String.join("\r\n",
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

    private List<String> getHeaderLines(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            headerLines.add(line);
            line = bufferedReader.readLine();
        }
        return headerLines;
    }

    private void processLogin(final RequestURL requestURL) {
        if (requestURL.isLoginRequest()) {
            final String account = requestURL.getParamValue("account");
            final String password = requestURL.getParamValue("password");
            checkUser(account, password);
        }
    }

    private void checkUser(final String account, final String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }

    private String checkContentType(final RequestURL requestURL) {
        String contentType = "text/html";
        if (requestURL.getPath().endsWith(".css")) {
            contentType = "text/css";
        }
        return contentType;
    }

    private String getResponseBody(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isMainRequest()) {
            return "Hello world!";
        }
        if (httpRequest.isLoginRequest()) {
            return "success";
        }
        String resource = httpRequest.getRequestURL().getPath();
        ClassLoader classLoader = this.getClass().getClassLoader();
        String file = classLoader.getResource("static" + resource).getFile();
        final Path path = Paths.get(file);
        return new String(Files.readAllBytes(path));
    }
}
