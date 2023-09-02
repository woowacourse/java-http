package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            List<String> request = readRequest(bufferedReader);

            var contentType = "html";
            for (String text : request) {
                if (text.startsWith("Accept:")) {
                    if (text.contains("css")) {
                        contentType = "css";
                    }
                    if (text.contains("js")) {
                        contentType = "javascript";
                    }
                }
            }

            var requestURI = request.get(0).split(" ")[1];
            QueryStrings queryStrings = QueryStrings.from(requestURI);
            if (queryStrings.getValues().containsKey("account")) {
                String account = queryStrings.getValues().get("account");
                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(IllegalArgumentException::new);
                log.info(user.toString());
            }
            if (requestURI.contains("?")) {
                final int index = requestURI.indexOf("?");
                requestURI = requestURI.substring(0, index) + ".html";
            }

            var responseBody = "Hello world!";
            if (!"/".equals(requestURI)) {
                final Path path =
                        new File(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + requestURI))
                                .getFile()
                        ).toPath();
                responseBody = new String(Files.readAllBytes(path));
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequest(BufferedReader bufferedReader) throws IOException {
        List<String> request = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            request.add(line);
        }
        return request;
    }

}
