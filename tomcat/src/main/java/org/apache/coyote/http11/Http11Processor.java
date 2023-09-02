package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            final HttpRequest httpRequest = HttpRequest.from(line);
            final var response = getResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(final HttpRequest httpRequest) {
        final HttpPath httpPath = httpRequest.getHttpPath();

        if (httpPath.isDefaultResource()) {
            final HttpResponse httpResponse = new HttpResponse(httpPath.getContentType(), "Hello world!");
            return httpResponse.extractResponse();
        }
        final HttpResponse httpResponse = new HttpResponse(httpPath.getContentType(), getResponseBody(httpPath));
        return httpResponse.extractResponse();
    }

    private String getResponseBody(final HttpPath httpPath) {
        Map<String, String> params = httpPath.getQueryParameter();
        if (!params.isEmpty() && httpPath.getResource().contains("login")) {
            executeLogin(params);
        }

        URL url = getClass().getClassLoader().getResource("static" + httpPath.getResource() + httpPath.getExtension());
        final Path path = new File(url.getPath()).toPath();

        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new UncheckedServletException("유효하지 않은 리소스에 대한 접근입니다.");
        }
    }

    private void executeLogin(Map<String, String> params) {
        final String userName = params.get("account");
        final String password = params.get("password");

        final User user = InMemoryUserRepository.findByAccount(userName)
                .orElseThrow(() -> new UncheckedServletException("존재하지 않는 userName입니다."));

        if (!user.checkPassword(password)) {
            throw new UncheckedServletException("비밀번호가 일치하지 않습니다.");
        }
        log.info(user.toString());
    }
}
