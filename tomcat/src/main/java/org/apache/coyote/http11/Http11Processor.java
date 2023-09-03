package org.apache.coyote.http11;

import javassist.NotFoundException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Params;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.Header;
import org.apache.coyote.http11.response.header.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final UserService userService;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        userService = new UserService();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            HttpRequest httpRequest = HttpRequest.from(getHeader(bufferedReader));
            HttpResponse httpResponse = getResponse(httpRequest);
            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | NotFoundException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getHeader(final BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();

        String line = Objects.requireNonNull(reader.readLine());
        while (!line.isEmpty()) {
            lines.add(line);
            line = reader.readLine();
        }

        return lines;
    }

    private HttpResponse getResponse(final HttpRequest request) throws URISyntaxException, IOException, NotFoundException {
        if (request.isSameUri("/")) {
            return HttpResponse.ok("Hello world!");
        }

        if (request.hasResource()) {
            return HttpResponse.okWithResource(request.getUri());
        }

        if (request.isSameUri("/login")) {
            return login(request.getParams());
        }

        throw new NotFoundException("페이지를 찾을 수 없습니다.");
    }

    private HttpResponse login(final Params queryParams) throws URISyntaxException, IOException, NotFoundException {
        if (queryParams.isEmpty()) {
            return HttpResponse.okWithResource("/login.html");
        }

        if (!queryParams.hasParam("account") || !queryParams.hasParam("password")) {
            throw new IllegalArgumentException("계정과 비밀번호를 입력하세요.");
        }

        userService.login(queryParams.get("account"), queryParams.get("password"));
        return HttpResponse.ok(ContentType.HTML, Status.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
    }
}
