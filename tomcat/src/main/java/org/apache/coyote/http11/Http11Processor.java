package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
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
    public static final String STATIC_PATH = "/static";

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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            Path path = Path.of("");
            StringBuilder responseBody = new StringBuilder();
            String contentType = "";
            while (bufferedReader.ready()) {  // TODO: 404 추가하기
                String line = bufferedReader.readLine();
                if (line.startsWith("GET")) {
                    String uri = line.split(" ")[1];
                    if (uri.equals("/")) {
                        contentType = "text/html; charset=utf-8 ";
                        path = Path.of(getClass().getResource(STATIC_PATH + "/index.html").getPath());
                    }
                    if (uri.startsWith("/login")) {
                        contentType = "text/html; charset=utf-8 ";
                        path = Path.of(getClass().getResource(STATIC_PATH + "/login.html").getPath());

                        int index = uri.indexOf("?");
                        String queryString = uri.substring(index + 1);
                        String[] userInfo = queryString.split("&");
                        String account = userInfo[0].split("=")[1];
                        String password = userInfo[1].split("=")[1];
                        User user = InMemoryUserRepository.findByAccount(account)
                                .orElseThrow();
                        if (user.checkPassword(password)) {
                            log.info(user.toString());
                        }
                    }
                    if (uri.endsWith(".html")) {
                        contentType = "text/html; charset=utf-8 ";
                        path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());
                    }
                    if (uri.endsWith(".css")) {
                        contentType = "text/css; charset=utf-8 ";
                        path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());

                    }
                    if (uri.endsWith(".js")) {
                        contentType = "application/javascript ";
                        path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());

                    }
                }
            }
            Files.readAllLines(path)
                    .stream()
                    .forEach(line -> responseBody.append(line).append("\n"));

            var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.toString().getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}

