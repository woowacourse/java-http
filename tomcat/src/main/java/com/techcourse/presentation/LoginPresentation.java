package com.techcourse.presentation;

import com.techcourse.infrastructure.Presentation;
import com.techcourse.presentation.requestparam.LoginRequestParam;
import com.techcourse.request.UserRequest;
import com.techcourse.service.LoginService;
import http.HttpHeader;
import http.HttpMethod;
import http.HttpStatusCode;
import java.util.List;
import org.apache.coyote.ioprocessor.parser.HttpRequest;
import org.apache.coyote.ioprocessor.parser.HttpResponse;

public class LoginPresentation implements Presentation {

    private static final String URI_PATH = "/login";

    private final LoginService loginService;

    public LoginPresentation(LoginService loginService) {
        this.loginService = loginService;
    }

    public LoginPresentation() {
        this(new LoginService());
    }

    @Override
    public HttpResponse view(HttpRequest request) {
        LoginRequestParam requestParam = new LoginRequestParam(request.getQueryParam());
        UserRequest userRequest = requestParam.toObject();
        String redirectionPage = processRedirectionPage(userRequest);
        List<HttpHeader> headers = List.of(new HttpHeader("Location", redirectionPage));
        return new HttpResponse(HttpStatusCode.FOUND, request.getMediaType(), headers);
    }

    private String processRedirectionPage(UserRequest request) {
        if (loginService.findUser(request)) {
            return "index.html";
        }
        return "401.html";
    }

    @Override
    public boolean match(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.GET
                && URI_PATH.equals(request.getPath())
                && !request.getQueryParam().isEmpty();
    }
}
