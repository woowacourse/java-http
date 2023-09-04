package org.apache.coyote.http11;

import javassist.NotFoundException;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Params;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.Header;
import org.apache.coyote.http11.response.header.Status;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;

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
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = getResponse(httpRequest);
            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | NotFoundException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(final HttpRequest httpRequest) throws URISyntaxException, IOException, NotFoundException {
        if (httpRequest.isSamePath("/")) {
            return HttpResponse.ok("Hello world!");
        }

        if (httpRequest.hasResource()) {
            return HttpResponse.okWithResource(httpRequest.getPath());
        }

        if (httpRequest.isSamePath("/login")) {
            return login(httpRequest);
        }

        if (httpRequest.isSamePath("/register") && httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
            return HttpResponse.okWithResource("/register.html");
        }

        if (httpRequest.isSamePath("/register") && httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
            Map<String, String> params = httpRequest.getBody().getParams();
            userService.register(params.get("account"), params.get("password"), params.get("email"));
            return HttpResponse.ok(ContentType.HTML, Status.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
        }

        throw new NotFoundException("페이지를 찾을 수 없습니다.");
    }

    private HttpResponse login(final HttpRequest httpRequest) throws URISyntaxException, IOException, NotFoundException {
        Params queryParams = httpRequest.getParams();

        if (queryParams.isEmpty() && httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
            if (httpRequest.getSessionAttribute("user").isPresent()) {
                return HttpResponse.ok(ContentType.HTML, Status.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
            }
            return HttpResponse.okWithResource("/login.html");
        }

        if (queryParams.isEmpty() && httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
            Map<String, String> params = httpRequest.getBody().getParams();

            if (params.isEmpty()) {
                throw new IllegalArgumentException("계정과 비밀번호를 입력하세요.");
            }

            User user;
            try {
                user = userService.login(params.get("account"), params.get("password"));
            } catch (UnAuthorizedException e) {
                return HttpResponse.unAuthorized("/401.html");
            }

            Session session = httpRequest.createSession();
            session.setAttribute("user", user);
            HttpResponse response = HttpResponse.ok(ContentType.HTML, Status.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
            response.setCookie(Cookie.fromUserJSession(session.getId()));
            return response;
        }

        throw new NotFoundException("페이지를 찾을 수 없습니다.");
    }
}
