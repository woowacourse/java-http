package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()) {

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final var requestLine = bufferedReader.readLine();
            final var method = requestLine.split(" ")[0];
            final var endpoint = requestLine.split(" ")[1];

            String line;
            int contentLength = 0;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            if (method.equals("POST") && endpoint.startsWith("/login")) {
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                final var requestBody = new String(buffer);

                String[] params = requestBody.split("&");
                String account = null;
                String password = null;

                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue[0].equals("account")) {
                        account = keyValue[1];
                    } else if (keyValue[0].equals("password")) {
                        password = keyValue[1];
                    }
                }

                final var user = InMemoryUserRepository.findByAccount(account);
                if (user.isPresent() && user.get().getAccount().equals(account)
                        && user.get().checkPassword(password)) {
                    final var response = String.join("\r\n",
                            "HTTP/1.1 302 FOUND ",
                            "Location: /index.html ",
                            "",
                            "");
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                } else {
                    final var path = Path.of(getClass().getResource("/static" + "/login.html").getPath());
                    final var responseBody = new String(Files.readAllBytes(path));

                    final var response = String.join("\r\n",
                            "HTTP/1.1 401 Unauthorized ",
                            "Content-Type: text/html;charset=utf-8 ",
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody);

                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }
            }

            if (method.equals("GET") && endpoint.equals("/")) {
                final var responseBody = "Hello world!";

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (method.equals("GET") && endpoint.equals("/css/styles.css")) {
                final var path = Path.of(getClass().getResource("/static" + endpoint).getPath());
                final var responseBody = new String(Files.readAllBytes(path));

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (method.equals("GET") && endpoint.startsWith("/login")) {
                final var urlPath = Path.of(getClass().getResource("/static" + "/login.html").getPath());
                final var responseBody = new String(Files.readAllBytes(urlPath));

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            final var path = Path.of(getClass().getResource("/static" + endpoint).getPath());
            final var responseBody = new String(Files.readAllBytes(path));

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
}
