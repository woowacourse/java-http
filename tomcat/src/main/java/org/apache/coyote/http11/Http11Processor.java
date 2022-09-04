package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.Header;
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
        try (final InputStream inputStream = connection.getInputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            final List<String> requestLines = readRequestLines(reader);
            final Request request = Request.from(requestLines);
            final String response = mapRequest(request).toText();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequestLines(final BufferedReader reader) throws IOException {
        final List<String> requestLines = new ArrayList<>();
        String line = Objects.requireNonNull(reader.readLine());
        while (!"".equals(line)) {
            requestLines.add(line);
            line = reader.readLine();
        }
        return requestLines;
    }

    private Response mapRequest(final Request request) throws URISyntaxException, IOException {
        try {
            return getResponse(request);
        } catch (NotFoundException e) {
            return new Response(StatusCode.NOT_FOUND, ResourceLoader.getStaticResource("/404.html"));
        } catch (IllegalArgumentException e) {
            return new Response(StatusCode.BAD_REQUEST, "잘못된 요청입니다.");
        } catch (UnauthorizedException e) {
            return new Response(StatusCode.UNAUTHORIZED, ResourceLoader.getStaticResource("/401.html"));
        }
    }

    private Response getResponse(final Request request) throws URISyntaxException, IOException {
        if (request.isPath("/")) {
            return Response.ofOk("Hello world!");
        }

        if (request.isForResource()) {
            return Response.ofResource(request.getPath());
        }

        if (request.isPath("/login")) {
            return login(request.getQueryParams());
        }

        throw new NotFoundException("페이지를 찾을 수 없습니다.");
    }

    private Response login(final QueryParams queryParams) throws URISyntaxException, IOException {
        if (queryParams.isEmpty()) {
            return Response.ofResource("/login.html");
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

        return new Response(ContentType.HTML, StatusCode.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
    }
}
