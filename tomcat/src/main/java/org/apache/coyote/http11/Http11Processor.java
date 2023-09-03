package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.SPACE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.QueryString;
import org.apache.coyote.http11.common.RequestHeader;
import org.apache.coyote.http11.common.RequestURI;
import org.apache.coyote.http11.common.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PAGE = "/login.html";
    private static final String LOGIN_FAIL_PAGE = "/401.html";
    private static final String QUERY_STRING_BEGIN_CHAR = "?";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var firstLine = bufferedReader.readLine();

            final var requestURI = readURI(firstLine);
            final var requestHeader = readHeader(bufferedReader);

            final var responseEntity = ResponseEntity.from(requestURI);
            final var response = responseEntity.getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestURI readURI(final String requestURILine) {
        final var requestURIElements = parseRequestURIElements(requestURILine);
        final var uri = requestURIElements.get(URI_INDEX);

        if (uri.startsWith("/login")) {
            return parseLoginRequestURI(uri, requestURIElements);
        }

        return parseRequestURI(uri, requestURIElements);
    }

    private List<String> parseRequestURIElements(final String requestURILine) {
        return Arrays.stream(requestURILine.split(SPACE))
                .collect(Collectors.toUnmodifiableList());
    }

    private RequestURI parseLoginRequestURI(final String uri, final List<String> requestURIElements) {
        final var index = uri.indexOf(QUERY_STRING_BEGIN_CHAR);
        final var queryString = QueryString.from(uri.substring(index + 1));

        final var account = queryString.getValue("account");
        final var password = queryString.getValue("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> loginSuccess(user, queryString, requestURIElements))
                .orElseGet(() -> loginFail(requestURIElements));
    }

    private RequestURI loginSuccess(
            final User user,
            final QueryString queryString,
            final List<String> requestURIElements
    ) {
        log.info("user : {}", user);
        return RequestURI.of(
                LOGIN_PAGE,
                queryString,
                requestURIElements.get(HTTP_METHOD_INDEX),
                requestURIElements.get(HTTP_VERSION_INDEX)
        );
    }

    private RequestURI loginFail(final List<String> requestURIElements) {
        return parseRequestURI(LOGIN_FAIL_PAGE, requestURIElements);
    }

    private RequestURI parseRequestURI(final String uri, final List<String> requestURIElements) {
        return RequestURI.of(
                uri,
                requestURIElements.get(HTTP_METHOD_INDEX),
                requestURIElements.get(HTTP_VERSION_INDEX)
        );
    }

    private RequestHeader readHeader(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String line = bufferedReader.readLine(); !"".equals(line); line = bufferedReader.readLine()) {
            stringBuilder.append(line).append(CRLF);
        }
        return RequestHeader.from(stringBuilder.toString());
    }

}
