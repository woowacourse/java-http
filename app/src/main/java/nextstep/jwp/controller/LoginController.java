package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.QueryStrings;
import nextstep.jwp.http.ResponseStatus;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController{

    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    private static final String URI_PATH = "/login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String SUCCESS_REDIRECT_URL = "http://localhost:8080/index.html";
    private static final String FAIL_REDIRECT_URL = "http://localhost:8080/401.html";

    @Override
    boolean isMatchingHttpMethod(HttpRequest httpRequest) {
        return HTTP_METHOD == httpRequest.getHttpMethod();
    }

    @Override
    boolean isMatchingUriPath(HttpRequest httpRequest) {
        return URI_PATH.equals(httpRequest.getPath());
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        if (httpRequest.hasQueryStrings()) {
            return findUserAndRedirect(httpRequest);
        }
        return super.renderPage(httpRequest.getPath());
    }

    private HttpResponse findUserAndRedirect(HttpRequest httpRequest) {
        try {
            validateUserInput(httpRequest.getQueryStrings());
            return redirect(SUCCESS_REDIRECT_URL);
        } catch (IllegalArgumentException e) {
            return redirect(FAIL_REDIRECT_URL);
        }
    }

    private void validateUserInput(QueryStrings queryStrings) {
        String account = queryStrings.getValue(ACCOUNT);
        String password = queryStrings.getValue(PASSWORD);

        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(IllegalArgumentException::new);

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException();
        }
    }

    private HttpResponse redirect(String redirectUrl) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Location", redirectUrl);

            return HttpResponse.status(ResponseStatus.FOUND, new HttpHeader(headers));
        } catch (IllegalArgumentException e) {
            // TODO: 에러 파일 출력
            return null;
        }
    }
}
