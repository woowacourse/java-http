package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final String requestLine = bufferedReader.readLine();
            final String[] requestLineComponents = requestLine.split(" ");
            final String requestURI = requestLineComponents[1];

            final var responseBody = readResource(requestURI);
            final String contentType = requestURI.endsWith(".css") ? "text/css" : "text/html;charset=utf-8";

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readResource(final String requestURI) throws IOException {
        if (requestURI.matches("/")) {
            return "Hello world!";
        }

        if (requestURI.startsWith("/login")) {
            if (requestURI.contains("?")) {
                loginAndRetrieveUserInfo(requestURI);
            }

            final String resourceURL = getClass().getClassLoader().getResource("static/login.html").getPath();
            final Path path = Path.of(resourceURL);

            return Files.readString(path);
        }

        // 자바 프로젝트를 빌드하면 src/main/resources 폴더 안에 있는 파일들이 빌드 결과물(클래스패스)의 최상위 루트로 복사된다. 따라서 앞에 /static을 붙여줘야 함
        final String resourceURL = getClass().getClassLoader().getResource("static" + requestURI).getPath();
        final Path path = Path.of(resourceURL);

        return Files.readString(path);
    }

    private void loginAndRetrieveUserInfo(final String requestURI) {
        final int index = requestURI.indexOf("?");
        final String[] loginInfos = requestURI.substring(index + 1).split("&");
        String account = "";
        String password = "";
        for (String loginInfo : loginInfos) {
            final String[] keyAndValue = loginInfo.split("=");
            final String key = keyAndValue[0];
            final String value = keyAndValue[1];
            if (key.matches("account")) {
                account = value;
                continue;
            }
            if (key.matches("password")) {
                password = value;
            }
        }

        if (!account.isBlank() && !password.isBlank()) {
            Optional<User> retrieveResult = InMemoryUserRepository.findByAccount(account);
            if (retrieveResult.isEmpty()) {
                return;
            }
            final User retrievedUser = retrieveResult.get();
            if (retrievedUser.checkPassword(password)) {
                System.out.println("user : " + retrievedUser);
            }
        }
    }
}
