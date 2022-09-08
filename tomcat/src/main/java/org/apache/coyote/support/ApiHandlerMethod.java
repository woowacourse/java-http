package org.apache.coyote.support;

import static java.util.Arrays.stream;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.constant.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ApiHandlerMethod {

    HOME_GET(HttpMethod.GET, "/") {
        @Override
        public void handle(final HttpRequest httpRequest, final HttpResponse response) {
            final String welcomeMessage = "Hello world!";
            response.addStatus(HttpStatus.OK)
                    .add(HttpHeader.CONTENT_TYPE, MediaType.PLAIN.value())
                    .body(welcomeMessage);
        }
    },

    LOGIN_POST(HttpMethod.POST, "/login") {
        @Override
        public void handle(final HttpRequest request, final HttpResponse response) {
            final Map<String, String> userMap = HttpParser.parseQueryString(request.getBody());
            final String account = userMap.get("account");
            final String password = userMap.get("password");
            final User foundUser = findUser(account);

            if (isLoginSuccess(foundUser, password)) {
                log.info("Login Success! {}", foundUser);
                loginSuccessEvent(foundUser, request, response);
                return;
            }

            loginFailEvent(request, response);
            log.info("Login Fail !");
        }

        private User findUser(final String account) {
            return InMemoryUserRepository.findByAccount(account)
                    .stream()
                    .findAny()
                    .orElse(null);
        }

        private boolean isLoginSuccess(final User findUser, final String password) {
            return findUser != null && findUser.checkPassword(password);
        }

        private void loginSuccessEvent(final User user, final HttpRequest request, final HttpResponse response) {
            final Session session = request.getSession(true);
            session.setAttribute("user", user);
            SessionManager.add(session);

            response.sendRedirect("/index.html")
                    .addCooke(HttpCookie.ofJSessionId(session.getId()));

            log.info("Redirect: /index.html");
        }

        private void loginFailEvent(final HttpRequest request, final HttpResponse response) {
            response.sendRedirect("/index.html");

            log.info("Redirect: /401.html");
        }
    },

    REGISTER_POST(HttpMethod.POST, "/register") {
        @Override
        public void handle(final HttpRequest request, final HttpResponse response) {
            final Map<String, String> userMap = HttpParser.parseQueryString(request.getBody());
            final String account = userMap.get("account");
            final String email = userMap.get("email");
            final String password = userMap.get("password");

            final User createUser = new User(account, password, email);

            InMemoryUserRepository.save(createUser);
            log.info("Create User: {}", createUser);

            response.sendRedirect("/index.html");
            log.info("Redirect: /index.html");
        }
    };

    private static final Logger log = LoggerFactory.getLogger(ApiHandlerMethod.class);

    private String httpMethod;
    private String uri;

    ApiHandlerMethod(final String httpMethod, final String uri) {
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    public abstract void handle(final HttpRequest request, final HttpResponse httpResponse);

    public static ApiHandlerMethod find(final HttpRequest httpRequest) {
        return stream(values())
                .filter(apiHandlerMethod -> httpRequest.isSame(apiHandlerMethod.httpMethod, apiHandlerMethod.uri))
                .findAny()
                .orElse(null);
    }
}
