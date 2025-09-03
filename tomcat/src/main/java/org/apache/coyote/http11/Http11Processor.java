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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String uri = br.readLine().split(" ")[1];

            if(uri.startsWith("/login")){
                int index = uri.indexOf("?");
                String queryString = uri.substring(index + 1);
                uri = "/login.html";

                Map<String, String> params = new HashMap<>();
                String[] pairs = queryString.split("&");
                for (String pair : pairs) {
                    String[] parts = pair.split("=");
                    if (parts.length == 2) {
                        params.put(parts[0], parts[1]);
                    }
                }

                String account = params.get("account");
                String password = params.get("password");
                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("그런 계정은 없다."));
                if(user.checkPassword(password)){
                    System.out.println(user);
                }
            }

            final var responseBody = new String(Files.readAllBytes(getStaticPath(uri)));
            String contentType = uri.split("\\.")[1];
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Path getStaticPath(String uri) throws URISyntaxException {
        return Paths.get(getClass().getClassLoader().getResource("static" + uri).toURI());
    }
}
