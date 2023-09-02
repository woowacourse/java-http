package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
             final var outputStream = connection.getOutputStream();
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader)) {
            String fileContent;
            HttpStatus httpStatus;
            String response;

            final Request request = Request.from(bufferedReader);

            if (request.getPath().startsWith("/login") && !request.getQueryParams().isEmpty()) {
                final Map<String, String> queryParams = request.getQueryParams();
                final User user = InMemoryUserRepository.findByAccount(queryParams.get("account")).orElseThrow();
                httpStatus = HttpStatus.FOUND;
                String redirectPath;
                if (user.checkPassword(queryParams.get("password"))) {
                    redirectPath = "/index.html";
                } else {
                    redirectPath = "/401.html";
                }
                response = new ResponseBuilder(request.getAccessType(), getFileContent(redirectPath), httpStatus)
                        .location(redirectPath)
                        .build();

            } else {
                httpStatus = HttpStatus.OK;
                fileContent = getFileContent(request.getPath());
                response = new ResponseBuilder(request.getAccessType(), fileContent, httpStatus).build();
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getFileContent(final String fileName) throws IOException {
        try {
            final URL resource = getClass().getClassLoader().getResource("static" + fileName);
            final Path path = Paths.get(resource.getPath());
            return Files.readString(path);
        } catch (final NullPointerException e) {
            log.error("error fileName = {}", fileName, e);
            return "";
        }
    }
}
