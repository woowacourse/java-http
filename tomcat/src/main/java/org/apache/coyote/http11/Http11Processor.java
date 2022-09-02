package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.FaviconNotFoundException;
import org.apache.coyote.http11.exception.InvalidHttpRequestStartLineException;
import org.apache.coyote.http11.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String FAVICON_URI = "/favicon.ico";
    private static final String QUERY_STRING_PREFIX = "?";
    private static final String HTML_EXTENSION = ".html";

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
            final String startLine = bufferedReader.readLine();
            validateStartLine(startLine);
            final List<String> lines = readLines(bufferedReader);
            final HttpRequest httpRequest = HttpRequest.of(startLine, lines);
            final String uri = parseUri(httpRequest);
            final HttpResponse httpResponse = createHttpResponse(httpRequest, uri);
            final String formattedResponse = httpResponse.format();
            log.info(httpRequest.toString());
            log.info(httpResponse.toString());

            outputStream.write(formattedResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | InvalidHttpRequestStartLineException | FaviconNotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void validateStartLine(final String startLine) {
        if (startLine == null) {
            throw new InvalidHttpRequestStartLineException();
        }
    }

    private List<String> readLines(final BufferedReader bufferedReader) throws IOException {
        final ArrayList<String> lines = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            lines.add(line);
        }

        return lines;
    }

    private String parseUri(final HttpRequest httpRequest) throws IOException {
        String uri = httpRequest.getUri();

        if (uri.equals(FAVICON_URI)) {
            throw new FaviconNotFoundException();
        }

        if (uri.contains(QUERY_STRING_PREFIX)) {
            int index = uri.indexOf(QUERY_STRING_PREFIX);
            String path = uri.substring(0, index);
            String queryString = uri.substring(index + 1);
            final QueryParameters queryParameters = QueryParameters.from(queryString);
            logUser(queryParameters);

            return path + HTML_EXTENSION;
        }

        return uri;
    }

    private void logUser(final QueryParameters queryParameters) {
        final String account = queryParameters.getValueByKey("account");
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        log.info(user.toString());
    }

    private HttpResponse createHttpResponse(final HttpRequest httpRequest, final String uri) throws IOException {
        System.err.println(httpRequest.toString());
        return new HttpResponse(HttpStatus.OK, httpRequest.getAcceptType(), ResourceLoader.getContent(uri));
    }
}
