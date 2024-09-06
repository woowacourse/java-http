package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = reader.readLine();

            String path = readPath(requestLine);

            if (path.equals("/")) {
                processHome(outputStream);
            }
            if (path.startsWith("/login")) {
                String method = readMethod(requestLine);
                processLogin(method, readBody(reader), outputStream);
            }
            if (path.equals("/register")) {
                String method = readMethod(requestLine);
                processRegister(method, readBody(reader), outputStream);
            }
            processFilesWithStatus(outputStream, path, HttpStatus.OK);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readPath(String requestLine) throws IOException {
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return null;
    }

    private String readMethod(String requestLine) throws IOException {
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length > 0) {
                return parts[0];
            }
        }
        return null;
    }

    private String readBody(BufferedReader reader) throws IOException {
        String line;
        int contentLength = 0;
        while (!(line = reader.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        if (contentLength > 0) {
            char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            return new String(body);
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

    private void processFilesWithStatus(OutputStream outputStream, String path, HttpStatus httpStatus) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + path);
        if (resource == null) {
            return;
        }
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final var response = String.join("\r\n",
                "HTTP/1.1 " + httpStatus.value() + " " + httpStatus.getReasonPhrase(),
                "Content-Type: text/" + readFileType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processFilesWithStatus(OutputStream outputStream, String path, HttpStatus httpStatus, String Cookie) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + path);
        if (resource == null) {
            return;
        }
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final var response = String.join("\r\n",
                "HTTP/1.1 " + httpStatus.value() + " " + httpStatus.getReasonPhrase(),
                "Set-Cookie:" + Cookie+ " ",
                "Content-Type: text/" + readFileType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processRegister(String method, String body, OutputStream outputStream) throws IOException {
        if (method.equals("GET")) {
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        }
        if (method.equals("POST")) {
            Map<String, List<String>> queryParams = parsingQueryString(body);
            String account = queryParams.get("account").get(0);
            String password = queryParams.get("password").get(0);
            String email = queryParams.get("email").get(0);
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            processFilesWithStatus(outputStream, "/index.html", HttpStatus.OK);
        }
    }

    private void processLogin(String method, String body, OutputStream outputStream) throws IOException {
        if (method.equals("GET")) {
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        }


        if (method.equals("POST")) {
            Map<String, List<String>> queryParams = parsingQueryString(body);
            User user = InMemoryUserRepository.findByAccount(queryParams.get("account").getFirst()).get();
            log.info("user : {}", user);
            if (!queryParams.containsKey("password")) {
                return;
            }
            if (user.checkPassword(queryParams.get("password").getFirst())) {
                processFilesWithStatus(outputStream, "/index.html", HttpStatus.valueOf(302), Http11Cookie.sessionCookie().toString());
                return;
            }
            processFilesWithStatus(outputStream, "/401.html", HttpStatus.valueOf(401));
        }

    }

    private Map<String, List<String>> parsingQueryString(String path) {
        Map<String, List<String>> queryParams = new HashMap<>();

        int questionMarkIndex = path.indexOf('?');
        String queryString;
        if (questionMarkIndex != -1) {
            queryString = path.substring(questionMarkIndex + 1);
        } else {
            queryString = path;
        }

        if (queryString.isEmpty()) {
            return Collections.emptyMap();
        }

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
