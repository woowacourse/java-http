package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;

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
        try (var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            String uri = getUri(inputStream);
            String path = uri;

            if ("/favicon.ico".equals(path)) {
                final var response = String.join("\r\n",
                        "HTTP/1.1 204 No Content",
                        "Content-Length: 0",
                        "",
                        "");
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            // TODO: 메서드 분리, /login 요청이 아닐 때 쿼리파라미터 처리 고민
            int questionMarkIndex = uri.indexOf("?");
            if (questionMarkIndex != -1) {
                path = uri.substring(0, questionMarkIndex) + ".html";

                String queryString = uri.substring(questionMarkIndex + 1);
                int ampersandIndex = queryString.indexOf("&");

                String accountKeyValue = queryString.substring(0, ampersandIndex);
                int accountEqualsIndex = accountKeyValue.indexOf("=");
                String accountKey = accountKeyValue.substring(0, accountEqualsIndex);
                String accountValue = accountKeyValue.substring(accountEqualsIndex + 1);
                if ("account".equals(accountKey)) {
                    InMemoryUserRepository.findByAccount(accountValue).ifPresent(user -> {
                        String passwordKeyValue = queryString.substring(ampersandIndex + 1);
                        int passwordEqualsIndex = passwordKeyValue.indexOf("=");
                        String passwordKey = passwordKeyValue.substring(0, passwordEqualsIndex);
                        String passwordValue = passwordKeyValue.substring(passwordEqualsIndex + 1);
                        if ("password".equals(passwordKey) && user.checkPassword(passwordValue)) {
                            log.info("user : {}", user);
                        }
                    });
                }
            }

            var responseBody = getStaticFileContent(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + getFileExtension(path) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getUri(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.readLine().split(" ")[1];
    }

    private String getStaticFileContent(String path) throws IOException {
        if (Objects.equals(path, "/")) {
            return "Hello world!";
        }
        String staticPath = "static/" + path;

        File file = new File(getClass().getClassLoader().getResource(staticPath).getPath());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getFileExtension(String path) {
        if (Objects.equals(path, "/")) {
            return "html";
        }
        String[] splitPath = path.split("\\.");
        return splitPath[splitPath.length - 1];
    }
}
