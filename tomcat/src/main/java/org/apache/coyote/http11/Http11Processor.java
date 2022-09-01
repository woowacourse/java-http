package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.StatusCode;
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
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            final String requestUri = parseUri(reader);
            log.info("request uri : {}", requestUri);

            if (reader.readLine() == null) {
                return;
            }

            final Map<String, String> header = parseHeader(reader);
            final String response = mapRequest(requestUri, header).toText();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUri(final BufferedReader reader) throws IOException {
        return reader.readLine().split(" ")[1];
    }

    private Map<String, String> parseHeader(final BufferedReader reader) throws IOException {
        final Map<String, String> header = new HashMap<>();
        String line = reader.readLine();
        while (!"".equals(line)) {
            final String[] splitLine = line.split(": ", 2);
            header.put(splitLine[0], splitLine[1]);
            line = reader.readLine();
        }
        return header;
    }

    private String parseContentType(final Map<String, String> header) {
        return header
                .getOrDefault("Accept", "text/html")
                .split(",")[0];
    }

    private Response mapRequest(final String requestUri, final Map<String, String> header)
            throws URISyntaxException, IOException {
        final String path = parsePath(requestUri);
        final Map<String, String> queryParams = parseQueryParams(requestUri);

        try {
            return getResponse(header, path, queryParams);
        } catch (NotFoundException e) {
            return new Response(ContentType.HTML, StatusCode.NOT_FOUND, ResourceLoader.getStaticResource("/404.html"));
        } catch (IllegalArgumentException e) {
            return new Response(ContentType.HTML, StatusCode.BAD_REQUEST, "잘못된 요청입니다.");
        } catch (UnauthorizedException e) {
            return new Response(ContentType.HTML, StatusCode.UNAUTHORIZED, ResourceLoader.getStaticResource("/401.html"));
        }
    }

    private String parsePath(final String requestUri) {
        final int queryStartIndex = requestUri.indexOf("?");
        if (queryStartIndex < 0) {
            return requestUri;
        }
        return requestUri.substring(0, queryStartIndex);
    }

    private Map<String, String> parseQueryParams(final String requestUri) {
        final int queryStartIndex = requestUri.indexOf("?");
        if (queryStartIndex < 0) {
            return new HashMap<>();
        }

        final String queryString = requestUri.substring(queryStartIndex + 1);
        return Arrays.stream(queryString.split("&"))
                .map(param -> param.split("=", 2))
                .filter(param -> param.length == 2)
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1]
                ));
    }

    private Response getResponse(final Map<String, String> header, final String path,
                                 final Map<String, String> queryParams) throws URISyntaxException, IOException {
        if (path.equals("/")) {
            return new Response(ContentType.HTML, StatusCode.OK, "Hello world!");
        }

        if (path.contains(".")) {
            return new Response(ContentType.of(parseContentType(header)), StatusCode.OK, ResourceLoader.getStaticResource(path));
        }

        if (path.equals("/login")) {
            return login(queryParams);
        }

        throw new NotFoundException("페이지를 찾을 수 없습니다.");
    }

    private Response login(final Map<String, String> queryParams) throws URISyntaxException, IOException {
        if (queryParams.isEmpty()) {
            return new Response(ContentType.HTML, StatusCode.OK, ResourceLoader.getStaticResource("/login.html"));
        }

        if (!queryParams.containsKey("account") || !queryParams.containsKey("password")) {
            throw new IllegalArgumentException("계정과 비밀번호를 입력하세요.");
        }

        return loginService(queryParams.get("account"), queryParams.get("password"));
    }

    private Response loginService(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("계정을 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("잘못된 비밀번호입니다.");
        }

        return new Response(ContentType.HTML, StatusCode.FOUND, Map.of("Location", "/index.html"), "");
    }
}
