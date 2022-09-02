package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.HttpCreator;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.io.ClassPathResource;
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
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = createHttpRequest(bufferReader);
            String httpResponse = createHttpResponse(httpRequest);

            printUser(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final BufferedReader bufferReader) throws IOException {
        return HttpCreator.createHttpRequest(bufferReader);
    }

    private String createHttpResponse(final HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        if (url.equals("/")) {
            return rootResponse(httpRequest);
        }
        String responseBody = new ClassPathResource().getFileContents(url);
        return okResponse(httpRequest.findContentType(), responseBody);
    }

    private String rootResponse(final HttpRequest httpRequest) {
        String responseBody = "Hello world!";
        return okResponse(httpRequest.findContentType(), responseBody);
    }

    private String okResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private void printUser(final HttpRequest httpRequest) {
        if (httpRequest.hasQueryString()) {
            Map<String, String> queryString = httpRequest.getQueryString();
            String account = queryString.get("account");
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            log.info(user.toString());
        }
    }
}
