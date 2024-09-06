package org.apache.coyote.http11;

import com.techcourse.HttpCookie;
import com.techcourse.HttpProtocol;
import com.techcourse.MyHttpRequest;
import com.techcourse.Session;
import com.techcourse.SessionManager;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

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

            // parse request
            MyHttpRequest request = new MyHttpRequest(inputStream);

            // handle post
            if ("POST".equals(request.getMethod())) {
                outputStream.write(handleStaticPostRequest(request));
                outputStream.flush();
                return;
            }

            log.info("request = {}", request);

            outputStream.write(handleStaticRequest(request));
            outputStream.flush();

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] handleStaticPostRequest(MyHttpRequest request) {
        Map<String, String> payload = request.getPayload();
        Session newId = new Session(UUID.randomUUID().toString());
        if (payload.containsKey("login")) {
            if (InMemoryUserRepository.findByAccount(payload.get("account")).isEmpty()) {
                throw new IllegalArgumentException("account already exists");
            }
            Session session = sessionManager.findSession(payload.get("account"));

            return String.join("\r\n",
                            "HTTP/1.1 " + "302 Found" + " ",
                            "Content-Type: " + "text/html" + " ",
                            "Location: /index.html" + " ",
                            "Set-Cookie: JSESSIONID=" + sessionManager.findSession(payload.get("account")) + " "
                    )
                    .getBytes();
        }

        if (InMemoryUserRepository.findByAccount(payload.get("account")).isPresent()) {
            throw new IllegalArgumentException("account already exists");
        }

        Session session = sessionManager.findSession(payload.get("account"));
        session.setAttribute("JESSIONID", UUID.randomUUID().toString());

        InMemoryUserRepository.save(
                new User(
                        payload.get("account"),
                        payload.get("password"),
                        payload.get("email")
                )
        );

        sessionManager.add(newId);
        return String.join("\r\n",
                        "HTTP/1.1 " + "302 Found" + " ",
                        "Content-Type: " + "text/html" + " ",
                        "Location: /index.html" + " ",
                        "Set-Cookie: JSESSIONID=" + newId + " "
                )
                .getBytes();
    }

    private byte[] handleStaticRequest(MyHttpRequest request) throws URISyntaxException, IOException {
        String location = request.getLocation();
        validateFileName(location);
        String[] split = location.split("\\.");
        String fileName = split[0];
        if (fileName.contains("?")) {
            return getQueryParameterResponseBody(fileName);
        }

        if (fileName.equals("/")) {
            fileName = "/index.html";
        } else if (split.length == 1) {
            fileName += ".html";
        } else if (split.length == 2) {
            fileName = fileName + "." + split[1];
        }

        return getResponseBody(fileName);
    }

    private byte[] getQueryParameterResponseBody(String query) throws URISyntaxException, IOException {
        String[] split = query.split("\\?");
        String fileName = split[0] + ".html";
        String[] keyValueArray = split[1].split("&");
        Map<String, String> keyValues = new HashMap<>();
        for (int i = 0; i < keyValueArray.length; i++) {
            String[] keyValueSplit = keyValueArray[i].split("=");
            keyValues.put(keyValueSplit[0], keyValueSplit[1]);
        }

        String statusCode = "200 OK";

        log.info("account={}", keyValues.get("account"));
        log.info("password={}", keyValues.get("password"));

        if (fileName.equals("/login.html")) {
            if (keyValues.get("account").equals("gugu") && keyValues.get("password").equals("password")) {
                statusCode = "302 Found";
                return String.join("\r\n",
                                "HTTP/1.1 " + statusCode + " ",
                                "Content-Type: " + "text/html" + " ",
                                "Location: /index.html" + " ")
                        .getBytes();
            } else {
                fileName = "/401.html";
                statusCode = "401 Unauthorized";
            }
        }

//        if (fileName.equals("/register.html")) {
////            System.out.println();
//            statusCode = "302 Found";
//            return String.join("\r\n",
//                            "HTTP/1.1 " + statusCode + " ",
//                            "Content-Type: " + "text/html" + " ",
//                            "Location: /index.html" + " ")
//                    .getBytes();
//        }

        String extension = fileName.split("\\.")[1];
        if (extension.equals("js")) {
            extension = "javascript";
        }
        String responseBody = Files.readString(
                Path.of(getClass().getClassLoader().getResource("static" + fileName).toURI())
        );
        return String.join("\r\n",
                        "HTTP/1.1 " + statusCode + " ",
                        "Content-Type: " + "text/" + extension + " ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "", responseBody)
                .getBytes();
    }

    private byte[] getResponseBody(String fileName) throws URISyntaxException, IOException {
        String responseBody = Files.readString(
                Path.of(getClass().getClassLoader().getResource("static" + fileName).toURI())
        );
        String extension = fileName.split("\\.")[1];
        if (extension.equals("js")) {
            extension = "javascript";
        }
        return String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + "text/" + extension + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "", responseBody)
                .getBytes();
    }

    private void validateFileName(String fileName) {
        try {
            getClass().getClassLoader().getResource("static" + fileName);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("file does not exist");
        }
    }
}
