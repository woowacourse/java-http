package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.coyote.http11.request.Cookie;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.ResponseBody;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.service.LoginService;

public class LoginController implements Controller {

    private static final String INVIDUAL_QUERY_PARAM_DIVIDER = "&";
    private static final String QUERY_PARAM_KEY_VALUE_SPLIT = "=";
    private static final int DONT_HAVE_VALUE = 1;
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOCATION_HEADER = "Location";

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Response<String> handle(Request request) {
        if (loggedin(request)) {
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .build();
        }

        Optional<String> loginSession = login(request);
        if (loginSession.isPresent()) {
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .addHeader("Set-Cookie", "JSESSIONID" + "=" + loginSession.get())
                .build();
        }

        return Response.status(302)
            .addHeader(LOCATION_HEADER, "/401.html")
            .build();
    }

    private boolean loggedin(Request request) {
        Optional<Cookie> cookie = request.getRequestHeaders().getCookie();
        if (cookie.isPresent()) {
            return cookie.get().getSessionCookie().isPresent();
        }
        return false;
    }

    private Optional<String> login(Request request) {
        Map<String, String> bodyData = convertBody(request.getResponseBody());
        String account = bodyData.get(ACCOUNT);
        String password = bodyData.get(PASSWORD);
        if (account != null && password != null) {
            return loginService.login(account, password);
        }
        return Optional.empty();
    }

    // TODO RequestLine 중복 로직 제거
    private Map<String, String> convertBody(ResponseBody responseBody) {
        return Stream.of(responseBody.getBody().split(INVIDUAL_QUERY_PARAM_DIVIDER))
            .collect(Collectors.toMap(this::keyOf, this::valueOf));
    }

    private String keyOf(String qp) {
        if (!qp.contains(QUERY_PARAM_KEY_VALUE_SPLIT)) {
            throw new IllegalArgumentException("유효하지 않은 key value 형식 입니다.");
        }
        String[] split = qp.split(QUERY_PARAM_KEY_VALUE_SPLIT);
        return split[0];
    }

    private String valueOf(String qp) {
        String[] split = qp.split(QUERY_PARAM_KEY_VALUE_SPLIT);
        if (split.length == DONT_HAVE_VALUE) {
            return "";
        }
        return split[1];
    }
}
