package org.apache.coyote.http11.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.coyote.http11.exception.MemberAlreadyExistsException;
import org.apache.coyote.http11.request.Cookies;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.ResponseBody;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.service.LoginService;
import org.apache.coyote.http11.session.SessionManager;

public class SignUpController implements Controller {

    private static final String INVIDUAL_QUERY_PARAM_DIVIDER = "&";
    private static final String QUERY_PARAM_KEY_VALUE_SPLIT = "=";
    private static final int DONT_HAVE_VALUE = 1;
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String LOCATION_HEADER = "Location";

    private final LoginService loginService;

    public SignUpController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Response handle(Request request) {
        try {
            if (loggedIn(request)) {
                return Response.status(302)
                    .addHeader(LOCATION_HEADER, "/index.html")
                    .build();
            }
            String loginSession = signUp(request);
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .addHeader("Set-Cookie", "JSESSIONID" + "=" + loginSession)
                .build();
        } catch (MemberAlreadyExistsException e) {
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/register.html")
                .build();
        }
    }

    private boolean loggedIn(Request request) {
        Optional<Cookies> cookie = request.getRequestHeaders().getCookie();
        if (cookie.isPresent()) {
            return checkSession(cookie.get());
        }
        return false;
    }

    private boolean checkSession(Cookies cookies) {
        Optional<String> sessionCookie = cookies.getSessionCookie();
        if (sessionCookie.isPresent()) {
            String sessionId = sessionCookie.get();
            HttpSession session = SessionManager.getInstance().findSession(sessionId);
            return session != null;
        }
        return false;
    }

    private String signUp(Request request) {
        Map<String, String> bodyData = convertBody(request.getResponseBody());
        String account = bodyData.get(ACCOUNT);
        String password = bodyData.get(PASSWORD);
        String email = bodyData.get(EMAIL);
        return loginService.signUp(account, password, email);
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
