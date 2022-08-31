package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {
    private static final int HTTP_STATUS_REDIRECTED = 302;
    private static final int HTTP_STATUS_UNAUTHORIZED = 401;

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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();

            if (line == null) {
                return;
            }

            Request request = new Request(line.split(" ")[1]);
            if (request.isValidLoginRequest()) {
                request = redirectAfterLogin(request);
            }

            final var response = createResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Request redirectAfterLogin(Request request) {
        Optional<User> user = InMemoryUserRepository.findByAccount(request.getQueryParam("account"));

        if(user.isPresent() && user.get().checkPassword(request.getQueryParam("password"))){
            log.info("user : " + user);
            return new Request("/index", HttpStatusCode.HTTP_STATUS_REDIRECTED);
        }
        return new Request("/401", HttpStatusCode.HTTP_STATUS_UNAUTHORIZED);
    }

    private String createResponse(Request request) throws IOException {

        final URL resource = getClass().getClassLoader().getResource(request.getStaticPath());
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        return String.join("\r\n",
            "HTTP/1.1 "+ request.getHttpStatusCode(),
            "Content-Type: " + request.getContentType() + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
