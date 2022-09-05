package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.HttpMessageDelimiter;
import org.apache.coyote.http11.mapping.HandlerMapping;
import org.apache.coyote.http11.mapping.MappingResponse;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        try (
            final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream();
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader)
        ) {

            final HttpRequest httpRequest = createHttpRequest(bufferedReader);
            final HttpResponse httpResponse = createHttpResponse(httpRequest);
            final String responseMessage = httpResponse.toMessage();

            printQueries(httpRequest);

            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final List<String> headers = extractMessage(bufferedReader);
        final List<String> bodies = extractMessage(bufferedReader);

        return HttpRequest.of(requestLine, headers, bodies);
    }

    private List<String> extractMessage(final BufferedReader bufferedReader) throws IOException {
        final List<String> messages = new ArrayList<>();
        if (!bufferedReader.ready()) {
            return messages;
        }

        for (String message = bufferedReader.readLine();
             bufferedReader.ready()
                 && !message.equals(HttpMessageDelimiter.HEADER_BODY.getValue());
             message = bufferedReader.readLine()) {

            messages.add(message);
        }
        return messages;
    }

    private HttpResponse createHttpResponse(final HttpRequest httpRequest) {
        final MappingResponse response = getResourceFromUrl(httpRequest.getUrl());
        final String resource = response.getResource();
        final String statusCode = response.getStatusCode();

        return HttpResponse.of(httpRequest.getHttpVersion(), resource, statusCode);
    }

    private MappingResponse getResourceFromUrl(final String url) {
        return HandlerMapping.getInstance()
            .getResponse(url);
    }

    private void printQueries(final HttpRequest httpRequest) {
        final Map<String, String> queries = httpRequest.getQueries();
        if (queries.isEmpty()) {
            return;
        }
        final User user = findUser(queries);
        log.info("user : {}", user);
    }

    private static User findUser(final Map<String, String> queries) {
        final IllegalArgumentException illegalLoginException = new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하였습니다.");

        final User user = InMemoryUserRepository.findByAccount(queries.get("account"))
            .orElseThrow(() -> illegalLoginException);

        if (!user.checkPassword(queries.get("password"))) {
            throw illegalLoginException;
        }
        return user;
    }
}
