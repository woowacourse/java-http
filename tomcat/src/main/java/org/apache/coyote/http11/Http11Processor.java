package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.sasl.AuthenticationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String request = bufferedReader.readLine();

            if (request == null) {
                return;
            }

            RequestStartLine requestStartLine = RequestStartLine.from(request);
            String response = createResponse(requestStartLine.getRequestURI());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(String requestURI) throws IOException {
        log.info("REQUEST URI: {}", requestURI);

        if (requestURI.equals("/")) {
            final var responseBody = "Hello world!";
            return generateResponse(requestURI, responseBody);
        }

        if (requestURI.equals("/login")) {
            Map<String, String> queryStrings = parseQueryString(requestURI);
            User account = findAccount(queryStrings);

            String password = queryStrings.get("password");
            boolean isCorrectPassword = account.checkPassword(password);
            if (!isCorrectPassword) {
                log.info("accout {} 비밀번호 불일치로 로그인 실패", account.getAccount());
                throw new AuthenticationException();
            }

            URL resource = getClass()
                    .getClassLoader()
                    .getResource("static" + requestURI + ".html");
            File file = new File(resource.getFile());
            String responseBody = new String(Files.readAllBytes(file.toPath()));
            return generateResponse(requestURI, responseBody);
        }

        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + requestURI);
        File file = new File(resource.getFile());
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        return generateResponse(requestURI, responseBody);
    }

    private String generateResponse(String requestURI, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType(requestURI) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private User findAccount(Map<String, String> queryStrings) {
        String account = queryStrings.get("account");
        Optional<User> findAccount = InMemoryUserRepository.findByAccount(account);
        if (findAccount.isEmpty()) {
            log.info("해당하는 Account를 찾을 수 없음! account={}", account);
            throw new NoSuchElementException();
        }

        return findAccount.get();
    }

    private Map<String, String> parseQueryString(String requestURI) {
        int questionIndex = requestURI.indexOf("?");
        if (questionIndex == -1) {
            return Map.of();
        }

        String nonParsedQueryString = requestURI.substring(questionIndex + 1);
        return Arrays.stream(nonParsedQueryString.split("&"))
                .map(it -> Arrays.stream(it.split("=")).collect(Collectors.toList()))
                .collect(Collectors.toMap(it -> it.get(0), it -> it.get(1)));
    }

    private String contentType(String requestURI) {
        if (requestURI.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
