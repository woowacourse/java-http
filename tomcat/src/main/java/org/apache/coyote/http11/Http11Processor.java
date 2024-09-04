package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }

            String page = getPage(firstLine);
            String responseBody = "";
            String response = "";
            if (page.equals("/")) {
                responseBody = "Hello world!";
                response = generateResponse(responseBody, "text/html");
            } else if (page.startsWith("/login?")) {
                int index = page.indexOf("?");
                String paths = page.substring(0, index);
                String queryString = page.substring(index + 1);
                String account = queryString.split("&")[0].split("=")[1];
                String password = queryString.split("&")[1].split("=")[1];

                User user = InMemoryUserRepository.findByAccount(account).get();
                if (user.checkPassword(password)) {
                    log.info("user : {}", user);
                }

                URL url = getClass().getClassLoader().getResource("static" + paths + ".html");
                if (url == null) {
                    return;
                }

                Path path = Path.of(url.toURI());
                responseBody = new String(Files.readAllBytes(path));
                response = generateResponse(responseBody, "text/html");
            } else if (page.startsWith("/css/")) {
                URL url = getClass().getClassLoader().getResource("static" + page);
                if (url == null) {
                    return;
                }

                Path path = Path.of(url.toURI());
                responseBody = new String(Files.readAllBytes(path));
                response = generateResponse(responseBody, "text/css");
            } else {
                URL url = getClass().getClassLoader().getResource("static" + page);
                if (url == null) {
                    return;
                }

                Path path = Path.of(url.toURI());
                responseBody = new String(Files.readAllBytes(path));
                response = generateResponse(responseBody, "text/html");
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPage(String firstLine) {
        return firstLine.split(" ")[1];
    }

    private String generateResponse(String responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
