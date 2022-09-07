package org.apache.coyote.http11;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public void processPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Map<String, String> queryParams = httpRequest.getRequestLine().getQueryParams();
        final User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .orElseThrow();

        final boolean isSuccessLogin = user.checkPassword(queryParams.get("password"));
        if (isSuccessLogin) {
            log.debug("로그인 성공 = {}", user);
        }
    }

    @Override
    public void processGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {

    }

}
