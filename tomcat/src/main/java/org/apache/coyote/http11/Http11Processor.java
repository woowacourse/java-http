package org.apache.coyote.http11;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String CONTENT_TYPE_FORMATTER = "Content-Type: text/%s;charset=utf-8 ";
    private static final String LOGIN_REQUEST_URI = "/login";
    private static final String ROOT_REQUEST_URI = "/";
    private static final int FILE_NAME_INDEX = 1;

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
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream();
             var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String requestFirstLine = bufferedReader.readLine();
            if (requestFirstLine == null) {
                return;
            }

            if (requestFirstLine.contains(LOGIN_REQUEST_URI)) {
                showUser(requestFirstLine);
                return;
            }

            var response = generateResponse(requestFirstLine);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void showUser(String resource) {
        int questionIndex = resource.indexOf("?");
        String queryString = resource.substring(questionIndex);
        String account = queryString.split("account=")[1].split("&")[0];
        String password = queryString.split("password=")[1].split(" ")[0];
        User user = findUser(account, password);
        log.debug(user.toString());
    }

    private User findUser(String account, String password) {
        User user = findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException();
        }
        return user;
    }

    private String generateResponse(String requestFirstLine) throws IOException {
        String resource = requestFirstLine.split(" ")[FILE_NAME_INDEX];
        String responseBody = generateResponseBody(resource);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                getContentType(resource),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getContentType(String resource) {
        if (resource.contains(ROOT_REQUEST_URI)) {
            return String.format(CONTENT_TYPE_FORMATTER, "html");
        }
        String fileExtension = resource.split(".")[1];
        return String.format(CONTENT_TYPE_FORMATTER, fileExtension);
    }

    private String generateResponseBody(String resource) throws IOException {
        if (resource.equals(ROOT_REQUEST_URI)) {
            return "Hello world!";
        }
        return generateResponseBodyByFile(resource);
    }

    private String generateResponseBodyByFile(String resource) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + resource);
        String file = Objects.requireNonNull(url)
                .getFile();
        Path path = new File(file).toPath();
        return Files.readString(path);
    }
}
