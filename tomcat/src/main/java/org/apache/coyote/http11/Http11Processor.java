package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import javassist.NotFoundException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT = "static";
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final String CONTENT_TYPE_CSS = "text/css";
    private static final String CONTENT_TYPE_JS = "application/javascript";

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
            String requestLine = br.readLine();

            var responseBody = "Hello world!";
            String contentType = CONTENT_TYPE_HTML;

            if (requestLine != null) {
                String[] words = requestLine.split(" ");

                if (!"/".equals(words[1])) {
                    String uri = words[1];
                    String uriForPath = uri;
                    int index = uri.indexOf("?");

                    if (uri.startsWith("/login")) {
                        uriForPath = "/login.html";
                    }

                    if (index != -1) {
                        String pathFromUri = uri.substring(0, index);
                        String queryString = uri.substring(index + 1);

                        if(pathFromUri.equals("/login") && !"".equals(queryString)) {
                            login(queryString);
                        }
                    }

                    var resource = ClassLoader.getSystemResource(ROOT + uriForPath);
                    Path path = Path.of(resource.toURI());
                    responseBody = Files.readString(path);

                    String[] splitUrl = uriForPath.split("\\.");
                    if (splitUrl.length > 1) {
                        contentType = findContentType(splitUrl[1]);
                    }
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void login(String query) {
        String account = parseLoginQuery(query, "account");
        String password = parseLoginQuery(query, "password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();

        if (user.checkPassword(password)) {
            log.info("user : {}", user.toString());
        }
    }

    private String parseLoginQuery(String query, String target) {
        String[] splitQuery = query.split("\\&");
        for (String q : splitQuery) {
            String[] keyAndValue = q.split("=");
            if (keyAndValue[0].startsWith(target)) {
                return keyAndValue[1];
            }
        }

        throw new IllegalArgumentException();
    }

    private String findContentType(String extension) {
        if (extension.equals("css")) {
            return CONTENT_TYPE_CSS;
        }

        if (extension.equals("js")) {
            return CONTENT_TYPE_JS;
        }

        return CONTENT_TYPE_HTML;
    }
}
