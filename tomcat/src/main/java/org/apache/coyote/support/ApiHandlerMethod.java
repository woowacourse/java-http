package org.apache.coyote.support;

import static java.util.Arrays.stream;
import static support.IoUtils.writeAndFlush;

import java.io.BufferedWriter;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.constant.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ApiHandlerMethod {

    HOME_GET(HttpMethod.GET, "/") {

        @Override
        public void handle(final HttpRequest httpRequest, final BufferedWriter bufferedWriter) {
            final String welcomeMessage = "Hello world!";
            final String response = HttpResponseBuilder.builder()
                    .addStatus(HttpStatus.OK)
                    .add(HttpHeader.CONTENT_TYPE, MediaType.PLAIN.value())
                    .body(welcomeMessage)
                    .build();

            writeAndFlush(bufferedWriter, response);
        }
    },

    LOGIN_POST(HttpMethod.POST, "/login") {

        @Override
        public void handle(final HttpRequest request, final BufferedWriter bufferedWriter) {
            final Map<String, String> userMap = HttpParser.parseQueryString(request.getBody());
            final String account = userMap.get("account");
            final String password = userMap.get("password");
            final User foundUser = findUser(account);

            if (isLoginSuccess(foundUser, password)) {
                log.info("Login Success! {}", foundUser);
                loginSuccessEvent(bufferedWriter, request, foundUser);
                return;
            }

            log.info("Login Fail !");
            loginFailEvent(bufferedWriter);
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

        /**
         * final var session = request.getSession(true);
         *     session.setAttribute("user", user);
         *     response.addCookie(Cookies.ofJSessionId(session.getId()));
         *     response.sendRedirect("/index.html");
         *     return;
         */
        private void loginSuccessEvent(final BufferedWriter bufferedWriter, final HttpRequest request, final User user) {
            // 이미 기 세션이 있다면 지울 것!
            final Session alreadyExistSession = request.getSession(false);
            SessionManager.remove(alreadyExistSession);

            // TODO login 폼에서 로그인후 다시 login폼에 왔을시에 JS=JS={id} 현싱
            final Session session = request.getSession(true);
            session.setAttribute("user", user);
            SessionManager.add(session);

            final String response = HttpResponseBuilder.builder()
                    .addStatus(HttpStatus.FOUND)
                    .add(HttpHeader.LOCATION, "/index.html")
                    .addCooke(session)
                    .build();

            writeAndFlush(bufferedWriter, response);
            log.info("Redirect: /index.html");
        }

        private void loginFailEvent(final BufferedWriter bufferedWriter) {
            final String response = HttpResponseBuilder.builder()
                    .addStatus(HttpStatus.FOUND)
                    .add(HttpHeader.LOCATION, "/401.html")
                    .build();

            writeAndFlush(bufferedWriter, response);
            log.info("Redirect: /401.html");
        }


    },

    REGISTER_POST(HttpMethod.POST, "/register") {

        @Override
        public void handle(final HttpRequest httpRequest, final BufferedWriter bufferedWriter) {
            final Map<String, String> userMap = HttpParser.parseQueryString(httpRequest.getBody());
            final String account = userMap.get("account");
            final String email = userMap.get("email");
            final String password = userMap.get("password");

            final User createUser = new User(account, password, email);

            InMemoryUserRepository.save(createUser);
            log.info("Create User: {}", createUser);

            final String response = HttpResponseBuilder.builder()
                    .addStatus(HttpStatus.FOUND)
                    .add(HttpHeader.LOCATION, "/index.html")
                    .build();

            writeAndFlush(bufferedWriter, response);
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

    public abstract void handle(final HttpRequest httpRequest, final BufferedWriter bufferedWriter);

    public static ApiHandlerMethod find(final HttpRequest httpRequest) {
        return stream(values())
                .filter(apiHandlerMethod -> httpRequest.isSame(apiHandlerMethod.httpMethod, apiHandlerMethod.uri))
                .findAny()
                .orElse(null);
    }
}
