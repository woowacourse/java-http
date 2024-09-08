package org.apache.coyote.http11.controller;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public enum LoginControllerAdapter {

    TRY_FIRST_LOGIN(HttpRequest::isQueryStringRequest, LoginControllerAdapter::checkLogin),
    SESSION_LOGIN(httpRequest -> httpRequest.hasCookie() && httpRequest.getCookie().has("JSESSIONID"),
            LoginControllerAdapter::checkSession),
    LOGIN_VIEW(httpRequest -> true, LoginControllerAdapter::loginView);

    private static LoginController loginController = LoginController.getInstance();
    private Predicate<HttpRequest> condition;
    private Function<HttpRequest, HttpResponse> handle;

    LoginControllerAdapter(Predicate<HttpRequest> condition, Function<HttpRequest, HttpResponse> handle) {
        this.condition = condition;
        this.handle = handle;
    }

    public static HttpResponse adapt(HttpRequest request) {
        return Stream.of(values())
                .filter(adapt -> adapt.condition.test(request))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("loginController에서 요청에 매칭되는 메서드가 없습니다."))
                .handle
                .apply(request);
    }


    private static HttpResponse checkSession(HttpRequest request) {
        return loginController.checkSession(request);
    }

    private static HttpResponse checkLogin(HttpRequest request) {
        return loginController.checkLogin(request);
    }

    private static HttpResponse loginView(HttpRequest request) {
        return loginController.loginView(request);
    }
}
