package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = reader.readLine();
            String[] tokens = requestLine.split(" ");

            String method = tokens[0];
            String path = tokens[1];

            Map<String, String> params = new HashMap<>();
            if(path.contains("?")) {
                String[] paramTokens = path.split("\\?");
                path = paramTokens[0];

                for(String param : paramTokens[1].split("&")) {
                    String[] pair = param.split("=");
                    if(pair.length == 2) {
                        params.put(pair[0], pair[1]);
                    }
                }
            }

            if (method.equals("GET") && path.equals("/index.html")) {
                final var fileStream = getClass().getClassLoader().getResourceAsStream("static/index.html");
                final var fileBytes = fileStream.readAllBytes();
                final var response = String.join(
                        "\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + fileBytes.length + " ",
                        "",
                        new String(fileBytes));

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (method.equals("GET") && path.equals("/css/styles.css")) {
                final var fileStream = getClass().getClassLoader().getResourceAsStream("static/css/styles.css");

                final var fileBytes = fileStream.readAllBytes();

                final var response = String.join(
                        "\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + fileBytes.length + " ",
                        "",
                        new String(fileBytes));

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if(method.equals("GET") && path.equals("/login")) {
                User user = InMemoryUserRepository.findByAccount(params.get("account"))
                        .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 틀렸습니다."));

                if(!user.checkPassword(params.get("password"))) {
                    throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
                }

                log.info(user.toString());

                final var fileStream = getClass()
                        .getClassLoader()
                        .getResourceAsStream("static/login.html");

                final var fileBytes = fileStream.readAllBytes();

                final var response = String.join(
                        "\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + fileBytes.length + " ",
                        "",
                        new String(fileBytes));

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }


            final var responseBody = "Hello world!";

            final var response = String.join(
                    "\r\n",
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
