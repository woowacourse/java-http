package org.apache.coyote.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class LoginController extends Controller {
    @Override
    public void processPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {

        final Map<String, String> queryParams = httpRequest.getRequestLine().getQueryParams();
        final User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .orElseThrow();

        final boolean isSuccessLogin = user.checkPassword(queryParams.get("password"));
        if (isSuccessLogin) {
        }

    }

    @Override
    public void processGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setView("login");
    }
}
