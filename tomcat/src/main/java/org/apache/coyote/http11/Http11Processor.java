package org.apache.coyote.http11;

import java.io.File;
import org.apache.coyote.http.ContentType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.ResponseBody;
import org.apache.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    public static final String STATIC_DIRECTORY = "static";
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
                httpResponse = HttpResponse.create200Response(ContentType.HTML, new ResponseBody(
                        new String(Files.readAllBytes(FileUtils.loadFile(STATIC_DIRECTORY + "/401.html").toPath()))));
                if (user.isPresent()) {
                    httpResponse = HttpResponse.create302Response(getResponseBody(httpRequest));
                }
                log.info(user.toString());
            }

            outputStream.write(httpResponse.convertTemplate().getBytes());
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
        return createResponse(httpRequest);
    }

    private HttpResponse createResponse(final HttpRequest httpRequest) throws IOException {
        final ResponseBody responseBody = getResponseBody(httpRequest);

        return HttpResponse.create200Response(ContentType.findContentType(httpRequest.getUrl()), responseBody);
    }

    private ResponseBody getResponseBody(final HttpRequest httpRequest) throws IOException {
        final File file = FileUtils.loadFile(STATIC_DIRECTORY + httpRequest.getUrl());
        return new ResponseBody(new String(Files.readAllBytes(file.toPath())));
    }

    private HttpResponse getHelloResponse() {
        final ResponseBody responseBody = new ResponseBody("Hello world!");
        return HttpResponse.create200Response(ContentType.HTML, responseBody);
    }
}
