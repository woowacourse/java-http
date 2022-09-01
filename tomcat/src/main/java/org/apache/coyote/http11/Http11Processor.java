package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            String uri = line.split(" ")[1];

            if(isLoginRequest(uri)) {
                uri = uri.substring(0, uri.lastIndexOf("?")) + ".html";
            }

            URL resource = getClass().getClassLoader().getResource("static" + uri);

            String fileName = Objects.requireNonNull(resource).getFile();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

            final Path path = Paths.get(Objects.requireNonNull(resource).getPath());

            final var responseBody = Files.readString(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + extension + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isLoginRequest(String uri) {
        int index = uri.indexOf("?");
        if(index == -1) {
            return false;
        }

        String path = uri.substring(0, index);
        if (!"/login".equals(path)) {
            return false;
        }

        String queryString = uri.substring(index + 1);
        String[] query = queryString.split("&");

        String account = query[0].split("=")[1];
        String password = query[1].split("=")[1];

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        System.out.println("user : " + user);

        return "gugu".equals(account) && "password".equals(password);
    }
}
