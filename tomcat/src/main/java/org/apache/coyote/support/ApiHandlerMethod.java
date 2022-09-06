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
            final String response = HttpResponse.builder()
                    .addStatus(HttpStatus.OK)
                    .add(HttpHeader.CONTENT_TYPE, MediaType.PLAIN)
                    .body(welcomeMessage)
                    .build();

            writeAndFlush(bufferedWriter, response);
        }
    },

    LOGIN_POST(HttpMethod.POST, "/login") {
        @Override
        public void handle(final HttpRequest httpRequest, final BufferedWriter bufferedWriter) {
            final Map<String, String> userMap = HttpParser.parseQueryString(httpRequest.getBody());
            final String account = userMap.get("account");
            final String password = userMap.get("password");
            final User foundUser = findUser(account);

            if (isLoginSuccess(foundUser, password)) {
                log.info("Login Success! {}", foundUser);
                loginSuccessEvent(bufferedWriter);
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

        private void loginSuccessEvent(final BufferedWriter bufferedWriter) {
            final String response = HttpResponse.builder()
                    .addStatus(HttpStatus.FOUND)
                    .add(HttpHeader.LOCATION, "/index.html")
                    .build();

            writeAndFlush(bufferedWriter, response);
            log.info("Redirect: /index.html");
        }

        private void loginFailEvent(final BufferedWriter bufferedWriter) {
            final String response = HttpResponse.builder()
                    .addStatus(HttpStatus.FOUND)
                    .add(HttpHeader.LOCATION, "/401.html")
                    .build();

            writeAndFlush(bufferedWriter, response);
            log.info("Redirect: /401.html");
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
