package org.apache.coyote.http11.url;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.HttpSession;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpDataRequest;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends Url {
    private static final Logger log = LoggerFactory.getLogger(Login.class);
    private static final String SESSION_KEY = "user";

    public Login(final String url, Http11Request request) {
        super(url, request);
    }

    @Override
    public Http11Response handle(HttpHeaders httpHeaders, String requestBody) {
        if (HttpMethod.GET.equals(request.getHttpMethod())) {
            try {
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute(SESSION_KEY);
                if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
                    return new Http11Response(getPath(), HttpStatus.FOUND, IOUtils.readResourceFile("/index.html"));
                }
            } catch (NullPointerException e) {
                log.info(e.getMessage(), e);
                return new Http11Response(getPath(), HttpStatus.OK, IOUtils.readResourceFile(getPath()));
            }
        }

        if (HttpMethod.POST.equals(request.getHttpMethod())) {
            HttpDataRequest data = HttpDataRequest.extractRequest(requestBody);
            User user = InMemoryUserRepository.findByAccount(data.get("account"))
                    .orElse(null);
            if (Objects.isNull(user)) {
                return new Http11Response(getPath(), HttpStatus.UNAUTHORIZED, IOUtils.readResourceFile("/401.html"));
            }
            if (user.checkPassword(data.get("password"))) {
                SessionManager sessionManager = new SessionManager();
                final var session = request.getSession();
                session.setAttribute("user", user);
                sessionManager.add(session);
                log.info("sessionManager에 저장 : {}", session);
                HttpCookie requestCookie = request.getCookie();
                HttpCookie responseCookie = requestCookie.addCookie(HttpCookie.ofJSessionId(session.getId()));
                return new Http11Response(getPath(), HttpStatus.FOUND, IOUtils.readResourceFile("/index.html"),
                        responseCookie);
            }
            log.info("user : {}", user);
            return new Http11Response(getPath(), HttpStatus.UNAUTHORIZED, IOUtils.readResourceFile("/401.html"));
        }
        throw new IllegalArgumentException("Login에 해당하는 HTTP Method가 아닙니다. : " + request.getHttpMethod());
    }
}
