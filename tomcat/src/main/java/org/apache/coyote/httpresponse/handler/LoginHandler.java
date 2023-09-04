package org.apache.coyote.httpresponse.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.QueryString;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final HttpResponse initialResponse = HttpResponse.init(request.getHttpVersion());
        final HttpResponse afterSetHttpStatus = initialResponse.setHttpStatus(HttpStatus.OK);
        final String resourcePath = request.getPath() + ".html";
        final HttpResponse afterSetContent = afterSetHttpStatus.setContent(resourcePath, request.getQueryString());
        final HttpResponse afterLogin = setLoginUser(request, afterSetContent);
        return afterLogin;
    }

    private HttpResponse setLoginUser(final HttpRequest request, final HttpResponse response) {
        final QueryString queryString = request.getQueryString();
        final String account = queryString.getValue("account");
        final String password = queryString.getValue("password");
        final Optional<User> searchedAccount = InMemoryUserRepository.findByAccount(account);
        if (searchedAccount.isPresent()) {
            final User user = searchedAccount.get();
            if (user.checkPassword(password)) {
                log.info(user.toString());
                return response;
            }
        }
        return new UnAuthorizedHandler().handle(request);
    }
}
