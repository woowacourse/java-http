package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.UserService;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.header.Header;
import org.apache.coyote.http11.message.request.QueryParams;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.header.ContentType;
import org.apache.coyote.http11.message.response.header.StatusCode;
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

            final Request request = Request.from(reader);
            log.info("request path: {}", request.getPath());
            final String response = mapRequest(request).toText();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
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
            return login(request.getUriQueryParams());
        }

        throw new NotFoundException("페이지를 찾을 수 없습니다.");
    }

    private Response login(final QueryParams queryParams) throws IOException, URISyntaxException {
        if (queryParams.isEmpty()) {
            return Response.ofResource("/login.html");
        }

        if (!queryParams.containsKey("account") || !queryParams.containsKey("password")) {
            throw new IllegalArgumentException("계정과 비밀번호를 입력하세요.");
        }

        UserService.login(queryParams.get("account"), queryParams.get("password"));
        return new Response(ContentType.HTML, StatusCode.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
    }
}
