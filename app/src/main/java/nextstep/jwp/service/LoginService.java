package nextstep.jwp.service;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);

    public void login(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        userLogin(httpRequest, httpResponse);
    }

    private void userLogin(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (isValidateUser(httpRequest)) {
            httpResponse.redirect302Transfer("/index.html");
        } else {
            httpResponse.redirectWithStatusCode("/401.html", "401");
        }
    }

    private boolean isValidateUser(final HttpRequest httpRequest) {
        User user = getUser(httpRequest);
        return user != null && isCollectPassword(httpRequest, user);
    }

    private boolean isCollectPassword(final HttpRequest httpRequest, final User user) {
        if (user.checkPassword(httpRequest.getQueryParam("password"))) {
            LOG.debug("password Collect! : {}", user.getAccount());
            return true;
        }
        LOG.debug("password InCollect!! : {}", user.getAccount());
        return false;
    }

    private User getUser(final HttpRequest httpRequest) {
        String requestUserAccount = httpRequest.getQueryParam("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getQueryParam("account"));
        if (user.isPresent()) {
            LOG.debug("user Account : {}", user.get().getAccount());
            return user.get();
        }
        LOG.debug("user Not Exist!! : {}", requestUserAccount);
        return null;
    }
}
