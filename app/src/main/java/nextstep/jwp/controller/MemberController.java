package nextstep.jwp.controller;

import nextstep.jwp.http.HttpContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

import java.util.Map;
import java.util.function.Function;

public class MemberController extends AbstractController {
    @Override
    void doGet(final HttpRequest request, HttpResponse response) {
        final Map<String, Function<HttpRequest, HttpResponse>> mappedFunction = Map.of(
                "login", this::getLogin,
                "register", this::getRegister
        );
        response.setResponse(getHttpResponse(request, mappedFunction).getResponse());
    }

    @Override
    void doPost(final HttpRequest request, HttpResponse response) {
        final Map<String, Function<HttpRequest, HttpResponse>> mappedFunction = Map.of(
                "login", this::postLogin,
                "register", this::postRegister
        );
        response.setResponse(getHttpResponse(request, mappedFunction).getResponse());
    }

    private HttpResponse getLogin(final HttpRequest request) {
        return new HttpResponse(HttpStatus.OK, HttpContentType.NOTHING, "login.html");
    }

    private HttpResponse getRegister(final HttpRequest request) {
        return new HttpResponse(HttpStatus.OK, HttpContentType.NOTHING, "register.html");
    }

    private HttpResponse postLogin(final HttpRequest request) {
        try {
            Map<String, String> requestParams = request.getRequestParams();
            User user = UserService.findUser(requestParams);
            log.info("로그인한 유저 : {}", user);
            return new HttpResponse(HttpStatus.FOUND, "index.html");
        } catch (IllegalArgumentException e) {
            return new HttpResponse(HttpStatus.UNAUTHORIZED, HttpContentType.NOTHING, "401.html");
        }
    }

    private HttpResponse postRegister(final HttpRequest request) {
        try {
            Map<String, String> params = request.parseRequestBodyParams();
            User user = UserService.registerUser(params);
            log.info("회원가입된 유저 : {}", user);
            return new HttpResponse(HttpStatus.CREATED, HttpContentType.NOTHING, "index.html");
        } catch (IllegalArgumentException e) {
            return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpContentType.NOTHING, "500.html");
        }
    }
}
