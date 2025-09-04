package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String startLine = bufferedReader.readLine();
            String[] startLines = startLine.split(" ");
            String requestMethod = startLines[0];
            String requestUrl = startLines[1];
            String requestHttpVersion = startLines[2];

            String response = null;
            if (requestMethod.equals("GET") && requestUrl.equals("/")) {
                final var responseBody = "Hello world!";

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }
            if (requestMethod.equals("GET") && requestUrl.equals("/index.html")) {
                URL resource = getClass().getClassLoader().getResource("static/index.html");
                Path resourcePath = Path.of(resource.getPath());
                byte[] bytes = Files.readAllBytes(resourcePath);

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + bytes.length + " ",
                        "",
                        new String(bytes));
            }
            if (requestMethod.equals("GET") && requestUrl.equals("/css/styles.css")) {
                URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
                Path resourcePath = Path.of(resource.getPath());
                byte[] bytes = Files.readAllBytes(resourcePath);

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + bytes.length + " ",
                        "",
                        new String(bytes));
            }
            if (requestMethod.equals("GET") && requestUrl.equals("/login?account=gugu&password=password")) {
                String uri = "/login?account=gugu&password=password";
                int index = uri.indexOf("?");
                String path = uri.substring(0, index);
                String queryStrings = uri.substring(index + 1);
                Map<String, String> queryStringMap = new HashMap<>();
                for (String queryString : queryStrings.split("&")) {
                    String[] strings = queryString.split("=");
                    String key = strings[0];
                    String value = strings[1];
                    queryStringMap.put(key, value);
                }
                User user = InMemoryUserRepository.findByAccount(queryStringMap.get("account")).get();
                if (user.checkPassword(queryStringMap.get("password"))) {
                    log.info("user = {}", user);
                }
                URL resource = getClass().getClassLoader().getResource("static/login.html");
                Path resourcePath = Path.of(resource.getPath());
                byte[] bytes = Files.readAllBytes(resourcePath);

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + bytes.length + " ",
                        "",
                        new String(bytes));
            }

            if (response != null) {
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
