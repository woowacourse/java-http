package org.apache.coyote.controller;

import nextstep.FileResolver;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.domain.HttpRequest;
import org.apache.coyote.http11.util.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class LoginController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String SET_COOKIE_PREFIX = "Set-Cookie: ";
    private static final String SESSION_NAME = "JSESSIONID=";
    public static final String RESPONSE_HEADER_200 = "HTTP/1.1 200 OK ";

    @Override
    public String run(final HttpRequest request) throws IOException {
        final String parsedUri = request.getUri();
        final String method = request.getMethod();
        if (HttpMethod.hasBody(method)) {
            return login(request.getBody());
        }
        final FileResolver file = FileResolver.findFile(parsedUri);
        return file.createResponse();
    }

    private String login(final Map<String, String> body) throws IOException {
        log.info("queryStrings = ", body);

        if (isValidUser(body)) {
            final String responseHeader = createJsession();
            return FileResolver.INDEX_HTML.createResponseWith(responseHeader);
        }
        return createRedirectResponse(FileResolver.HTML_401);
    }

    private String createJsession() {
        final UUID session = createSession();
        return String.join("\r\n",
                RESPONSE_HEADER_200,
                SET_COOKIE_PREFIX + SESSION_NAME + session
        );
    }

    private UUID createSession() {
        return UUID.randomUUID();
    }

    private boolean isValidUser(final Map<String, String> body) {
        final User account = InMemoryUserRepository.findByAccount(body.get("account"))
                                                   .orElseThrow(() -> new IllegalArgumentException("잘못된 유저 정보입니다."));
        return account.checkPassword(body.get("password"));
    }

    private String createRedirectResponse(final FileResolver file) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + file.getFileName() + " ",
                ""
        );
    }
}
