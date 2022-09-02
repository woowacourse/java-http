package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.util.ResourcesUtil;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String HTTP_VERSION = "HTTP/1.1";

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            HttpRequest httpRequest = HttpRequest.from(line);

            printLoginUser(httpRequest);

            final var response = createResponseBody(httpRequest);

            outputStream.write(response.httpResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponseBody(final HttpRequest httpRequest) {
        if (httpRequest.isRootPath()) {
            return helloResponse();
        }
        String responseBody = ResourcesUtil.readResource(httpRequest.getFilePath(), this.getClass());
        return okResponse(httpRequest.getContentType(), responseBody);
    }

    private HttpResponse helloResponse() {
        var responseBody = "Hello world!";
        return okResponse(ContentType.TEXT_HTML, responseBody);
    }

    private HttpResponse okResponse(final ContentType contentType, final String responseBody) {
        return new HttpResponse(HTTP_VERSION, HttpStatus.OK, contentType, responseBody);
    }

    private void printLoginUser(final HttpRequest httpRequest) {
        if (httpRequest.getPath().equals("/login.html")) {
            Map<String, String> queryParams = httpRequest.getQueryParams();
            String account = queryParams.get("account");
            String password = queryParams.get("password");
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(NotFoundUserException::new);
            if (user.checkPassword(password)) {
                log.info("user : {}", user);
            }
        }
    }
}
