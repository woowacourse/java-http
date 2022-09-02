package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String resource = getResource(bufferedReader);
            resource = changeResource(resource);

            if (resource.contains("?")) {
                int index = resource.indexOf("?");
                String path = resource.substring(0, index);
                String queryString = resource.substring(index + 1);
                parsingQueryString(path, queryString);
            }

            String responseBody = getResponseBody(resource);

            String contentType = checkContentType(resource);

            var response = String.join("\r\n",
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

    private String getResource(BufferedReader bufferedReader) throws IOException {
        String requestURI = bufferedReader.readLine();
        return requestURI.split(" ")[1]
                .substring(1);
    }

    private String changeResource(String resource) {
        if (resource.equals("login")) {
            resource += ".html";
        }
        return resource;
    }

    private void parsingQueryString(String path, String queryString) {
        if (path.equals("login")) {
            String account = queryString.split("&")[0].split("=")[1];
            String password = queryString.split("&")[1].split("=")[1];
            checkUser(account, password);
        }
    }

    private void checkUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }

    private String checkContentType(String resource) {
        String contentType = "text/html";
        if (resource.endsWith(".css")) {
            contentType = "text/css";
        }
        return contentType;
    }

    private String getResponseBody(String resource) throws IOException {
        if (resource.isEmpty()) {
            return "Hello world!";
        }
        if (resource.contains("?")) {
            return "success";
        }
        Path path = Paths.get(this.getClass().getClassLoader().getResource("static/" + resource).getFile());
        return new String(Files.readAllBytes(path));
    }
}

