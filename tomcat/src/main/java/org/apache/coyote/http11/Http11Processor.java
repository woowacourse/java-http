package org.apache.coyote.http11;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

import org.apache.coyote.Processor;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private static final Map<String, String> httpRequestHeader = new HashMap<>();
    private static final String sessionId = "JSESSIONID=sessionId";

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


            String line = reader.readLine();
            String httpMethod = line.split(" ")[0];
            String urlPath = line.split(" ")[1];

            while((line = reader.readLine()) != null && !line.isEmpty()) {
                httpRequestHeader.put(line.split(":")[0].trim(), line.split(":")[1].trim());
            }

            if(urlPath.endsWith("html")) {
                printFileResource("static" + urlPath,  outputStream);
                return;
            }
            if(urlPath.startsWith("/login")) {
                if(urlPath.equals("/login") && httpMethod.equals("GET")) {
                    if(httpRequestHeader.get("Cookie") != null) {
                        Optional<String> sessionCookie = Arrays.asList(httpRequestHeader.get("Cookie").split("; "))
                            .stream()
                            .filter(cookie -> cookie.startsWith("JSESSIONID"))
                            .findAny();
                        if(!sessionCookie.isEmpty()) {
                            String sessionId = sessionCookie.get().split("=")[1];
                            Optional<User> userBySession = SessionManager.findUserBySession(sessionId);
                            if(userBySession.isPresent()) {
                                redirect("http://localhost:8080/index.html", outputStream);
                                return;
                            }
                        }
                    }
                    printFileResource("static" + urlPath +".html",  outputStream);
                    return;
                }
                String body = parseBody(reader);
                if (body != null) {
                    System.out.println("body = " + body);
                    String account = body.split("&")[0].split("=")[1];
                    String password = body.split("&")[1].split("=")[1];

                    User user = InMemoryUserRepository.findByAccount(account)
                        .orElse(new User("guest", "guest", "guest"));
                    if (user.checkPassword(password)) {
                        SessionManager.createSession(user);
                        redirectWithSetCookie("http://localhost:8080/index.html", outputStream);
                        return;
                    }
                }
                redirect("http://localhost:8080/401.html", outputStream);
                return;
            }
            if(urlPath.startsWith("/register")) {
                if(httpMethod.equals("GET")) {
                    printFileResource("static" + urlPath + ".html", outputStream);
                    return;
                }
                String body = parseBody(reader);
                if (body != null) {
                    String account = body.split("&")[0].split("=")[1];
                    String mail = body.split("&")[1].split("=")[1];
                    String password = body.split("&")[2].split("=")[1];
                    User user = new User(account, mail, password);
                    InMemoryUserRepository.save(user);
                    redirect("http://localhost:8080/index.html", outputStream);
                    return;
                }
            }

            if(urlPath.startsWith("/css") || urlPath.startsWith("/js") || urlPath.startsWith("/assets")) {
                printFileResource("static" + urlPath, outputStream);
                return;
            }

            final var responseBody = "Hello world!";

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseBody(BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeader.get("Content-Length"));
        if(contentLength > 0) {
            char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            return new String(body);
        }
        return null;
    }

    private void printFileResource(String fileName, OutputStream outputStream) {
        final URL url = getClass().getClassLoader().getResource(fileName);
        final File file = new File(url.getPath());
        final Path path = file.toPath();

        try {
            String contentType = "text/html";
            if(fileName.endsWith("css")) {
                contentType = "text/css";
            } else if(fileName.endsWith("js")) {
                contentType = "application/javascript";
            }

            final var responseBody = new String(Files.readAllBytes(path));
            var response = "HTTP/1.1 200 OK \r\n" +
                String.format("Content-Type: %s;charset=utf-8 \r\n", contentType) +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n"+
                responseBody;
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void redirect(String location, OutputStream outputStream) {
        try {
            String contentType = "text/html";
            var response = "HTTP/1.1 302 Found \r\n" +
                "Location: " + location + "\r\n" +
                String.format("Content-Type: %s;charset=utf-8 \r\n", contentType) +
                "Content-Length: 0";

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
		}
	}

    private void redirectWithSetCookie(String location, OutputStream outputStream) {
        try {
            String contentType = "text/html";
            var response = "HTTP/1.1 302 Found \r\n" +
                "Set-Cookie: " + sessionId + " \r\n" +
                "Location: " + location + " \r\n" +
                String.format("Content-Type: %s;charset=utf-8 \r\n", contentType) +
                "Content-Length: 0";

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
