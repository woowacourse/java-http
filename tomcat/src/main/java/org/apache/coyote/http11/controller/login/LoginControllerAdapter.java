package org.apache.coyote.http11.controller.login;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public enum LoginControllerAdapter {

    TRY_FIRST_LOGIN(
            HttpRequest::isQueryStringRequest,
            (request, response) -> LoginController.getInstance().checkLogin(request, response)
    ),
    SESSION_LOGIN(
            httpRequest -> httpRequest.hasCookie() && httpRequest.getCookie().has("JSESSIONID"),
            (request, response) -> LoginController.getInstance().checkSession(request, response)
    ),
    LOGIN_VIEW(
            httpRequest -> true,
            (request, response) -> LoginController.getInstance().loginView(request, response)
    );

    private Predicate<HttpRequest> condition;
    private BiConsumer<HttpRequest, HttpResponse> handle;

    LoginControllerAdapter(Predicate<HttpRequest> condition, BiConsumer<HttpRequest, HttpResponse> handle) {
        this.condition = condition;
        this.handle = handle;
    }

    public static void adapt(HttpRequest request, HttpResponse response) {
        Stream.of(values())
                .filter(adapt -> adapt.condition.test(request))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("loginController에서 요청에 매칭되는 메서드가 없습니다."))
                .handle
                .accept(request, response);
    }
}
