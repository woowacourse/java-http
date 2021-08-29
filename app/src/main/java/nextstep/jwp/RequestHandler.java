package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nextstep.jwp.controller.GetController;
import nextstep.jwp.controller.PostController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> request = new ArrayList<>();
            while (bufferedReader.ready()) {
                request.add(bufferedReader.readLine());
            }
            if (request.size() == 0 ){
                return;
            }
            HttpRequestHeader httpRequestHeader = new HttpRequestHeader(request);

            String httpMethod = httpRequestHeader.getHttpMethod();
            String requestURI = httpRequestHeader.getRequestURI();

            if ("GET".equals(httpMethod)) {
                GetController.run(requestURI, outputStream, getClass().getClassLoader());
            }

            if ("POST".equals(httpMethod)) {
                PostController.run(requestURI, outputStream, getClass().getClassLoader(), httpRequestHeader);
            }


        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
