package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final var response = responseBuilder(bufferedReader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String responseBuilder(final BufferedReader bufferedReader) throws IOException {
        final String BASIC_RESPONSE_BODY = "Hello world!";
        final String BASIC_RESPONSE = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + BASIC_RESPONSE_BODY.getBytes().length + " ",
                "",
                BASIC_RESPONSE_BODY);

        List<String> request = new ArrayList<>();

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            request.add(line.concat("\r\n"));
        }

        List<String> requestUri = List.of(request.getFirst().split(" "));

        if (requestUri.get(1).equals("/index.html")) {
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody
                    );
        }

        if (requestUri.get(1).equals("/css/styles.css")) {
            final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;*/*;q=0.1 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody
            );
        }

        if (requestUri.get(1).endsWith(".js")) {
            final URL resource = getClass().getClassLoader().getResource("static" + requestUri.get(1));
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: application/javascript;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody
            );
        }

        if (requestUri.get(1).startsWith("/login")) {
            return login(requestUri);
        }

        return BASIC_RESPONSE;
    }

    private String login(List<String> loginRequest) throws IOException {
        String uris = loginRequest.get(1);
        int index = uris.indexOf("?");
        String uri = uris.substring(0, index);
        String queryString = uris.substring(index + 1);
        final URL resource = getClass().getClassLoader().getResource("static" + uri + ".html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        List<String> infos = List.of(queryString.split("&"));
        List<String> ids = List.of(infos.get(0).split("="));
        List<String> passwords = List.of(infos.get(1).split("="));
        User user = InMemoryUserRepository.findByAccount(ids.get(1)).get();
        if (user.checkPassword(passwords.get(1))) {
            log.info(user.toString());
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody
            );
        }
        return "";
    }
}
