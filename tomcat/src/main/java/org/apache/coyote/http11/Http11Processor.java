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

            final String line = bufferedReader.readLine();

            final HttpRequest httpRequest = HttpRequest.from(line);

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
            return generateResponseMessage(path.getContentType(), "Hello world!");
        }
        if (path.isIcoContentType()) {
            return "";
        }
        return generateResponseMessage(path.getContentType(), generateResponseBody(path));
    }

    private String generateResponseMessage(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String generateResponseBody(Path path) {
        Map<String, String> params = path.getQueryParameter();
        if (!params.isEmpty() && path.getResource().contains("login")) {
            processLogin(params);
            return ResourceFindUtils.getResourceFile(path.getResource() + path.getExtension());
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
