package org.apache.coyote.http11.responseGenerator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.utils.QueryParamsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPostResponseMaker implements ResponseMaker {

    private static final Logger log = LoggerFactory.getLogger(LoginGetResponseMaker.class);

    @Override
    public String createResponse(final HttpRequest httpRequest)
            throws URISyntaxException, IOException {
        final HashMap<String, String> loginData = QueryParamsParser.parseByBody(httpRequest.getRequestBody());
        final URL resource =
                this.getClass().getClassLoader().getResource("static" + "/login.html");
        final Path path = Paths.get(resource.toURI());
        final var responseBody = new String(Files.readAllBytes(path));
        return makeLoginResponse(loginData, responseBody);
    }

    private String makeLoginResponse(
            final HashMap<String, String> loginData, final String responseBody
    ) {
        final String account = loginData.get("account");
        final String password = loginData.get("password");
        if (loginData.isEmpty() || !validateAccount(account, password)) {
            log.info("로그인 데이터가 없습니다.");
            return failLoginResponse(responseBody);
        }
        final User user = new User(account, password);
        return successLoginResponse(responseBody, user);
    }

    private String successLoginResponse(final String responseBody, final User user) {
        final HttpResponse httpResponse =
                new HttpResponse(HttpStatus.FOUND, responseBody, ContentType.HTML, "/index.html");
        final Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);
        httpResponse.addJSessionId(session);
        return httpResponse.toString();
    }

    private String failLoginResponse(final String responseBody) {
        log.info("로그인 계정 정보가 이상합니다. responseBody={}", responseBody);
        final HttpResponse httpResponse =
                new HttpResponse(HttpStatus.FOUND, responseBody, ContentType.HTML, "/401.html");
        return httpResponse.toString();
    }

    private boolean validateAccount(final String account, final String password) {
        final Optional<User> accessUser = InMemoryUserRepository.findByAccount(account);
        return accessUser.filter(user -> validatePassword(password, user))
                .isPresent();
    }

    private boolean validatePassword(final String password, final User accessUser) {
        if (!accessUser.checkPassword(password)) {
            return false;
        }
        log.info("user = {}", accessUser);
        return true;
    }
}
