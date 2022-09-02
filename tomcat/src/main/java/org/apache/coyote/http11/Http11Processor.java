package org.apache.coyote.http11;

import org.apache.coyote.http.ContentType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            HttpResponse httpResponse = createHttpResponse(httpRequest);

            if (httpRequest.hasQueryString()) {
                Map<String, String> queryString = httpRequest.getQueryString();
                String account = queryString.get("account");
                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                log.info(user.toString());
            }

            outputStream.write(httpResponse.getTemplate().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createHttpResponse(final HttpRequest httpRequest) throws URISyntaxException, IOException {
        final String url = httpRequest.getUrl();
        if (url.equals("/")) {
            return getHelloResponse();
        }
        return createResponse(url);
    }

    private HttpResponse createResponse(final String requestUrl)
            throws URISyntaxException, IOException {
        final URL resource = this.getClass().getClassLoader().getResource("static" + requestUrl);
        final Path path = Paths.get(resource.toURI());
        final String responseBody = new String(Files.readAllBytes(path));

        return new HttpResponse("HTTP/1.1",
                "200",
                "OK",
                ContentType.findContentType(requestUrl),
                responseBody.getBytes().length,
                responseBody);
    }

    private HttpResponse getHelloResponse() {
        String responseBody = "Hello world!";
        return new HttpResponse("HTTP/1.1",
                "200",
                "OK",
                ContentType.HTML,
                responseBody.getBytes().length,
                responseBody);
    }
}
