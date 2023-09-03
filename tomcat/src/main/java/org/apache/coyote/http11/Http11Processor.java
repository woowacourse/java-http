package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.NotCorrectPasswordException;
import org.apache.coyote.http11.exception.NotFoundAccountException;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login.html";
    private static final String ROOT_RESOURCE = "Hello world!";
    private static final String STATIC_PATH = "static";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

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

            final HttpRequest httpRequest = HttpRequest.from(inputStream);
            final String response = getResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getResponse(final HttpRequest httpRequest) throws IOException {
        final String content = getContent(httpRequest);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + HttpContentType.valueOfCotentType(httpRequest.getRequestURL().getExtension()).getContentType(),
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
    }

    public String getContent(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getRequestURL().getUrl().equals(ROOT_PATH)) {
            return ROOT_RESOURCE;
        }
        final String requestUrl = httpRequest.getRequestURL().getAbsolutePath();
        if (requestUrl.equals(LOGIN_PATH)) {
            return Resource.readResource(login(requestUrl, httpRequest.getRequestURL().getRequestParam()));
        }
        return Resource.readResource(STATIC_PATH + requestUrl);
    }

    private String login(final String url, final Map<String, String> requestParam) {
        if (!requestParam.isEmpty()) {
            final User user = InMemoryUserRepository.findByAccount(requestParam.get(ACCOUNT))
                    .orElseThrow(NotFoundAccountException::new);

            if (!user.checkPassword(requestParam.get(PASSWORD))) {
                throw new NotCorrectPasswordException();
            }
            log.info(user.toString());
        }
        return STATIC_PATH + url;
    }
}
