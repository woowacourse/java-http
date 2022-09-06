package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.Builder;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final String response = getResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequest httpRequest) {
        Path path = httpRequest.getPath();
        if (path.getResource().equals("/")) {
            final HttpResponse httpResponse = new Builder()
                    .status(HttpStatus.OK)
                    .contentType(path.getContentType())
                    .responseBody("Hello world!")
                    .build();

            return httpResponse.toResponseMessage();
        }
        if (path.isIcoContentType()) {
            return "";
        }
        return generateResponseMessage(path.getContentType(), generateResponseBody(path));
    }

    private String generateResponseMessage(String contentType, String responseBody) {
        return new HttpResponse.Builder()
                .status(HttpStatus.OK)
                .contentType(contentType)
                .responseBody(responseBody)
                .build()
                .toResponseMessage();
    }

    private String generateResponseBody(Path path) {
        Map<String, String> params = path.getQueryParameter();
        if (!params.isEmpty() && path.getResource().contains("login")) {
            processLogin(params);
        }
        return ResourceFindUtils.getResourceFile(path.getResource() + path.getExtension());
    }

    private void processLogin(Map<String, String> params) {
        final String username = params.get("account");
        final String password = params.get("password");

        User user = InMemoryUserRepository.findByAccount(username)
                .orElseThrow(LoginException::new);

        if (!user.checkPassword(password)) {
            throw new LoginException();
        }
        log.info(user.toString());
    }
}
