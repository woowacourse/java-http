package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.DocFlavor.READER;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            String path = readPath(inputStream);
            if (path.equals("/")) {
                processHome(outputStream);
            }
            if (path.startsWith("/login")) {
                processLogin(outputStream, path);
            }
            processFiles(outputStream, path);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readPath(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine();

        if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return null;
    }

    private String readFileType(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == path.length() - 1) {
            return "";
        }
        return path.substring(lastDotIndex + 1);
    }

    private void processHome(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processFiles(OutputStream outputStream, String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + path);
        if (resource == null) {
            return;
        }
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + readFileType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processLogin(OutputStream outputStream, String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        Map<String, List<String>> queryParams = parsingQueryString(path);

        if (queryParams.containsKey("account")) {
            User user = InMemoryUserRepository.findByAccount(queryParams.get("account").getFirst()).get();
            log.info("user : {}", user);
            if (!queryParams.containsKey("password")) {
                return;
            }
            if (user.checkPassword(queryParams.get("password").getFirst())) {
                processFiles(outputStream, "/index.html");
                return;
            }
            processFiles(outputStream, "/js/401.html");
        }

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private Map<String, List<String>> parsingQueryString(String path) {
        Map<String, List<String>> queryParams = new HashMap<>();

        int questionMarkIndex = path.indexOf('?');
        if (questionMarkIndex == -1 || questionMarkIndex == path.length() - 1) {
            return Collections.emptyMap();
        }

        String queryString = path.substring(questionMarkIndex + 1);
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length != 2) {
                continue;
            }
            String key = keyValue[0];
            String value = keyValue[1];
            queryParams.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        return queryParams;
    }
}
