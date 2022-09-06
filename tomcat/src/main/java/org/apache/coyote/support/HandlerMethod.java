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

public enum HandlerMethod {

    HOME_GET(HttpMethod.GET, "/") {
        @Override
        public void service(final HttpRequest httpRequest, final BufferedWriter bufferedWriter) {
            final String welcomeMessage = "Hello world!";
            final String response = HttpResponse.builder()
                    .add(HttpHeader.HTTP_1_1_STATUS_CODE, "200 OK")
                    .add(HttpHeader.CONTENT_TYPE, MediaType.PLAIN)
                    .body(welcomeMessage)
                    .build();

            writeAndFlush(bufferedWriter, response);
        }
    },

    LOGIN_POST(HttpMethod.POST, "/login") {
        @Override
        public void service(final HttpRequest httpRequest, final BufferedWriter bufferedWriter) {
            final Map<String, String> userMap = HttpParser.parseQueryString(httpRequest.getPostContent());
            final String account = userMap.get("account");
            final String password = userMap.get("password");
            final User foundUser = findUser(account);

            if (isLoginSuccess(foundUser, password)) {
                log.info("Login Success! {}", foundUser);
                loginSuccessEvent(bufferedWriter, foundUser);
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

        private void loginSuccessEvent(final BufferedWriter bufferedWriter, final User foundUser) {
//            final String response0 = getRedirectResponseForLoginSuccess("302 Found", "/index.html");
            final String response = HttpResponse.builder()
                    .add(HttpHeader.HTTP_1_1_STATUS_CODE, "302 Found")
                    .add(HttpHeader.LOCATION, "/index.html")
                    .build();

            writeAndFlush(bufferedWriter, response);
            log.info("Redirect: /index.html");
        }

        private void loginFailEvent(final BufferedWriter bufferedWriter) {
//            final String response0 = getRedirectResponse("302 Found", "/401.html");
            final String response = HttpResponse.builder()
                    .add(HttpHeader.HTTP_1_1_STATUS_CODE, "302 Found")
                    .add(HttpHeader.LOCATION, "/401.html")
                    .build();

            writeAndFlush(bufferedWriter, response);
            log.info("Redirect: /401.html");
        }

        private String getRedirectResponseForLoginSuccess(final String status, final String redirectUrl) {
            return String.join(
                    "\r\n",
                    "HTTP/1.1 " + status + " ",
                    "Location: " + redirectUrl + " ",
                    "");
        }

        private String getRedirectResponse(final String status, final String redirectUrl) {
            return String.join(
                    "\r\n",
                    "HTTP/1.1 " + status + " ",
                    "Location: " + redirectUrl + " ",
                    "");
        }
    };

    private static final Logger log = LoggerFactory.getLogger(HandlerMethod.class);

    private String httpMethod;
    private String uri;

    HandlerMethod(final String httpMethod, final String uri) {
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    public abstract void service(final HttpRequest httpRequest, final BufferedWriter bufferedWriter);

    public static HandlerMethod find(final HttpRequest httpRequest) {
        return stream(values())
                .filter(handlerMethod -> httpRequest.isSame(handlerMethod.httpMethod, handlerMethod.uri))
                .findAny()
                .orElse(null);
    }

    private static String getResponse(final String responseBody, final String fileType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
