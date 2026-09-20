package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final String uri = bufferedReader.readLine().split(" ")[1].substring(1);
            int index = uri.indexOf("?");
            String path = uri;
            String queryString = "";
            if (index != -1) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            }
            HashMap<String, String> params = parseQueryString(queryString);
            if (path.equals("login")) {
                path = "login.html";
                if (params.containsKey("account")) {
                    User existUser = InMemoryUserRepository.findByAccount(params.get("account"))
                            .filter(user -> user.checkPassword(params.get("password")))
                            .orElse(null);
                    String location = "/401.html";
                    if (existUser != null) {
                        log.info("user : {}", existUser);
                        location = "/index.html";
                    }
                    final var redirectResponse = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: " + location + " ",
                            "",
                            "");
                    outputStream.write(redirectResponse.getBytes());
                    outputStream.flush();
                    return;
                }
            }

            final URL url = getClass().getClassLoader().getResource("static/" + path);

            final var responseBody = Files.readAllBytes(Path.of(url.toURI()));

            final String contentType = path.substring(path.lastIndexOf(".") + 1);
            String responseContentType = "text/html;charset=utf-8";
            if (contentType.equals("css")) {
                responseContentType = "text/css;charset=utf-8";
            }
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + responseContentType + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, String> parseQueryString(String queryString) {
        HashMap<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}
