package org.apache.coyote.http11.auth;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.response.ResponsePage.CONFLICT_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.INDEX_PAGE;

public class RegisterService {

    public HttpResponse getIndexOrConflictResponse(HttpRequest request, HttpResponse response) {
        final String account = request.requestBody().getBy("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return HttpResponse.getCookieNullResponseEntity(request.getProtocol(), CONFLICT_PAGE);
        }

        final String email = request.requestBody().getBy("email");
        final String password = request.requestBody().getBy("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponse.getCookieNullResponseEntity(request.getProtocol(), INDEX_PAGE);
    }

}
