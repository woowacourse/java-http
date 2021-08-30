package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.infrastructure.http.request.HttpMethod;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;
import nextstep.jwp.infrastructure.http.view.ResourceView;
import nextstep.jwp.infrastructure.http.view.View;

public class GetLoginController implements Controller {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final List<String> REQUIRED_PARAMETERS = Arrays.asList(ACCOUNT, PASSWORD);

    @Override
    public HttpRequestLine requestLine() {
        return new HttpRequestLine(HttpMethod.GET, "/login");
    }

    @Override
    public View handle(final HttpRequest request) {
        return new ResourceView("/login.html");
    }
}
