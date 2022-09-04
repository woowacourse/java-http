package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpResponseCreator;
import nextstep.jwp.io.ClassPathResource;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "login";
    private static final String ROOT_RESPONSE_BODY = "Hello world!";

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
            HttpResponse httpResponse = createHttpResponse(httpRequest);

            printUser(httpRequest);

            outputStream.write(httpResponse.getResponseTemplate().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final BufferedReader bufferReader) throws IOException {
        return new HttpRequestCreator().createHttpRequest(bufferReader);
    }

    private HttpResponse createHttpResponse(final HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if (path.equals(ROOT_PATH)) {
            return HttpResponseCreator.okResponse(httpRequest.findContentType(), ROOT_RESPONSE_BODY);
        }
        String responseBody = new ClassPathResource().getStaticContent(path);
        return HttpResponseCreator.okResponse(httpRequest.findContentType(), responseBody);
    }

    private void printUser(final HttpRequest httpRequest) {
        if (httpRequest.hasQueryParams()) {
            Map<String, String> queryString = httpRequest.getQueryParams();
            String account = queryString.get("account");
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            log.info(user.toString());
        }
    }
}
