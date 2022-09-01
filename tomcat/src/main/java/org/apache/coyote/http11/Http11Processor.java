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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String fileName = bufferedReader.readLine().split(" ")[1];

            String responseBody = generateBody(fileName);
            String last = parseExtension(fileName);
            login(fileName);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + last + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateBody(String fileName) throws IOException {
        if (fileName.equals("/")) {
            return "Hello world!";
        }
        String extension = parseExtension(fileName);
        String newFileName = fileName.split("\\?")[0].substring(1);
        if (!newFileName.contains(extension)) {
            newFileName += "." + extension;
        }
        URL resource = getClass().getClassLoader().getResource("static/" + newFileName);
        if (resource == null) {
            return "Hello world!";
        }
        final Path path = new File(resource.getFile()).toPath();
        final List<String> actual = Files.readAllLines(path);
        return String.join("\n", actual) + "\n";
    }

    private String parseExtension(String fileName) throws IOException {
        if (fileName.equals("/")) {
            return "html";
        }
        URL resource = getClass().getClassLoader().getResource("static/" + fileName.substring(1));
        if (resource == null) {
            return "html";
        }
        return fileName.split("\\.")[1];
    }

    private void login(String fileName) {
        if (!fileName.contains("?")) {
            return;
        }
        String[] parsed = fileName.split("\\?");
        if (!parsed[0].equals("/login")) {
            return;
        }
        String[] rawQueryString = parsed[1].split("&");
        Map<String, String> queryString = new HashMap<>();
        for (String eachParsed : rawQueryString) {
            String[] secondParsed = eachParsed.split("=");
            queryString.put(secondParsed[0], secondParsed[1]);
        }
        if (!queryString.containsKey("account") || !queryString.containsKey("password")) {
            return;
        }
        Optional<User> account = InMemoryUserRepository.findByAccount(queryString.get("account"));
        if (account.isEmpty()) {
            return;
        }
        User user = account.get();
        if (!user.checkPassword(queryString.get("password"))) {
            return;
        }
        log.info("user : {}", user);
    }
}
