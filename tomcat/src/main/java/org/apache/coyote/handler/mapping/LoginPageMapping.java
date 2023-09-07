package org.apache.coyote.handler.mapping;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeaders;
import org.apache.coyote.http.request.HttpCookie;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http.common.HttpHeader.COOKIE;

public class LoginPageMapping extends LoginFilter implements HandlerMapping {

    public static final String TARGET_URI = "login";
    private static final Logger log = LoggerFactory.getLogger(LoginPageMapping.class);

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() && httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.containsHeader(COOKIE)) {
            final HttpCookie cookies = HttpCookie.from(httpRequest.getHeader(COOKIE));
            if (isAlreadyLogined(cookies.get("JSESSIONID"))) {
                return HttpResponse.redirect("/index.html");
            }
        }

        final String[] parsedRequestUri = httpRequest.getRequestUri().getRequestUri().split("\\?");
        if (httpRequest.getRequestUri().getRequestUri().contains("?")) {
            final Map<String, String> queryStrings = Arrays.stream(parsedRequestUri[1].split("&"))
                    .map(param -> param.split("="))
                    .collect(Collectors.toMap(param -> param[0], param -> param[1]));

            final String account = queryStrings.get("account");
            final String password = queryStrings.get("password");

            try {
                final User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("잘못된 계정입니다. 다시 입력해주세요."));

                if (!user.checkPassword(password)) {
                    throw new IllegalArgumentException("잘못된 비밀번호입니다. 다시 입력해주세요.");
                }
                log.info("로그인 성공! user = {}", user);
            } catch (final IllegalArgumentException e) {
                log.warn("login error = {}", e);
                return HttpResponse.redirect("/401.html");
            }

            return HttpResponse.redirect("/index.html");
        }

        return HttpResponse.builder()
                .statusLine(StatusLine.from(StatusCode.OK))
                .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.HTML.getValue())))
                .body(HttpBody.file("static/login.html"))
                .build();
    }

}
