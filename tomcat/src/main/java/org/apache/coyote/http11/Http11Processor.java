package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.mapping.HandlerMapping;
import org.apache.coyote.http11.mapping.MappingResponse;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestGenerator;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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

            final HttpRequest httpRequest = HttpRequestGenerator.createHttpRequest(bufferedReader);
            final HttpResponse httpResponse = HttpResponseGenerator.createHttpResponse(httpRequest);
            final String responseMessage = httpResponse.toMessage();

            printQueries(httpRequest);

            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
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
