package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);

            Map<String, String> httpRequestHeaders = new HashMap<>();
            String firstLine = br.readLine();
            String mimeType = "";
            String requestBody = "";
            String responseBody = "";
            String response = "";

            if (firstLine == null) {
                return;
            }

            String[] firstLines = firstLine.split(" ");
            httpRequestHeaders.put(firstLines[0], firstLines[1]);

            while (true) {
                String line = br.readLine();

                if ("".equals(line)) {
                    break;
                }

                String[] strings = line.split(": ");
                httpRequestHeaders.put(strings[0], strings[1]);
            }

            if (firstLines[0].equals("POST")) {
                int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                br.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
            }

            if (firstLines[1].equals("/")) {
                mimeType = "text/html";
                responseBody = "Hello world!";
                response = getOKResponse(mimeType, responseBody);
            } else if (firstLines[1].startsWith("/css")) {
                mimeType = "text/css";

                URL resource = getClass().getClassLoader().getResource("static" + firstLines[1]);
                File file = new File(resource.getFile());
                Path path = file.toPath();
                responseBody = new String(Files.readAllBytes(path));
                response = getOKResponse(mimeType, responseBody);
            } else if (firstLines[1].startsWith("/login") && firstLines[0].equals("POST")) {
                String account = requestBody.split("&")[0].split("=")[1];
                String password = requestBody.split("&")[1].split("=")[1];

                Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    if (user.checkPassword(password)) {
                        log.info(user.toString());
                        String uuid = UUID.randomUUID().toString();
                        Session session = new Session(uuid);
                        session.setAttribute("user", session);
                        SessionManager.add(session);
                        response = getRedirectResponse(uuid, responseBody, "/index.html");
                    } else {
                        response = getRedirectResponse(responseBody, "/401.html");
                    }
                } else {
                    response = getRedirectResponse(responseBody, "/401.html");
                }
            } else if (firstLines[1].equals("/register") && firstLines[0].equals("POST")) {
                String account = requestBody.split("&")[0].split("=")[1];
                String password = requestBody.split("&")[1].split("=")[1];
                String email = requestBody.split("&")[2].split("=")[1];

                InMemoryUserRepository.save(new User(account, password, email));
                response = getRedirectResponse(UUID.randomUUID().toString(), responseBody, "/index.html");
            } else {
                mimeType = "text/html";

                if (firstLines[1].equals("/login") || firstLines[1].equals("/register")) {
                    if (httpRequestHeaders.containsKey("Cookie")) {
                        String[] cookies = httpRequestHeaders.get("Cookie").split(";");

                        for (String cookie : cookies) {
                            if (cookie.trim().startsWith("JSESSIONID")) {
                                String uuid = cookie.split("=")[1];
                                if (SessionManager.findSession(uuid) != null) {
                                    response = getRedirectResponse(uuid, mimeType, responseBody);
                                } else {
                                    firstLines[1] = firstLines[1] + ".html";
                                    URL resource = getClass().getClassLoader().getResource("static" + firstLines[1]);
                                    File file = new File(resource.getFile());
                                    Path path = file.toPath();
                                    responseBody = new String(Files.readAllBytes(path));
                                    response = getOKResponse(mimeType, responseBody);
                                }
                            }
                        }
                    } else {
                        firstLines[1] = firstLines[1] + ".html";
                        URL resource = getClass().getClassLoader().getResource("static" + firstLines[1]);
                        File file = new File(resource.getFile());
                        Path path = file.toPath();
                        responseBody = new String(Files.readAllBytes(path));
                        response = getOKResponse(mimeType, responseBody);
                    }
                } else {
                    URL resource = getClass().getClassLoader().getResource("static" + firstLines[1]);
                    File file = new File(resource.getFile());
                    Path path = file.toPath();
                    responseBody = new String(Files.readAllBytes(path));
                    response = getOKResponse(mimeType, responseBody);
                }
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getOKResponse(String mimeType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getRedirectResponse(String responseBody, String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "",
                responseBody);
    }

    private String getRedirectResponse(String sessionId, String responseBody, String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Set-Cookie: JSESSIONID=" + sessionId + " ",
                "Location: " + location + " ",
                "",
                responseBody);
    }
}
