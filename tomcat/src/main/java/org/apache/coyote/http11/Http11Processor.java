package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String[] requestHeaderInfo = br.readLine().split(" ");
            String url = requestHeaderInfo[1].substring(1);

            if (url.isEmpty()) {
                sendDefaultResource(outputStream);
                return;
            }

            String staticUrl = "static/" + url;

            if (url.contains("login")) {
                int index = url.indexOf("?");
                staticUrl = "static/" + url.substring(0, index) + ".html";
                authenticateUser(url, index);
            }

            sendStaticResource(staticUrl, outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDefaultResource(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
        sendResponse(outputStream, response);
    }

    private void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void authenticateUser(String url, int index) {
        String queryString = url.substring(index + 1);
        String accountQuery = queryString.split("&")[0];
        String passwordQuery = queryString.split("&")[1];
        String account = accountQuery.split("=")[1];
        String password = passwordQuery.split("=")[1];

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지않는 계정입니다."));

        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }

    private void sendStaticResource(String staticUrl, OutputStream outputStream) throws URISyntaxException, IOException {
        final URI uri = findUri(staticUrl);
        final Path path = Paths.get(uri);
        final var response = getResponse(path);
        sendResponse(outputStream, response);
    }

    private URI findUri(String staticUrl) throws URISyntaxException {
        try{
            return getClass().getClassLoader().getResource(staticUrl).toURI();
        } catch (NullPointerException e) {
            return getClass().getClassLoader().getResource("static/404.html").toURI();
        }
    }

    private String getResponse(Path path) throws IOException {
        final var contentType = Files.probeContentType(path);
        final String responseBody = Files.readString(path);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
