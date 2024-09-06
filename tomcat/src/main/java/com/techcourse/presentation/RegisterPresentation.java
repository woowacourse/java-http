package com.techcourse.presentation;

import com.techcourse.infrastructure.Presentation;
import com.techcourse.presentation.requestparam.RegisterRequestParam;
import com.techcourse.request.RegisterRequest;
import com.techcourse.service.RegisterService;
import http.HttpHeader;
import http.HttpMethod;
import http.HttpStatusCode;
import java.util.List;
import org.apache.coyote.ioprocessor.parser.HttpRequest;
import org.apache.coyote.ioprocessor.parser.HttpResponse;

public class RegisterPresentation implements Presentation {

    private static final String URI_PATH = "/register";

    private final RegisterService registerService;

    public RegisterPresentation(RegisterService registerService) {
        this.registerService = registerService;
    }

    public RegisterPresentation() {
        this(new RegisterService());
    }

    @Override
    public HttpResponse view(HttpRequest request) {
        RegisterRequestParam registerRequestParam = new RegisterRequestParam(request.getQueryParam());
        RegisterRequest registerRequest = registerRequestParam.toObject();
        String redirectionPage = processRedirectionPage(registerRequest);
        List<HttpHeader> headers = List.of(new HttpHeader("Location", redirectionPage));
        return new HttpResponse(HttpStatusCode.FOUND, request.getMediaType(), headers);
    }

    private String processRedirectionPage(RegisterRequest request) {
        if (registerService.register(request)) {
            return "index.html";
        }
        return "401.html";
    }

    @Override
    public boolean match(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.POST
                && URI_PATH.equals(request.getPath())
                && !request.getQueryParam().isEmpty();
    }
}
