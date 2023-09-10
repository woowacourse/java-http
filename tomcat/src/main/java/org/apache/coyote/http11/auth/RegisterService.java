package org.apache.coyote.http11.auth;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.response.ResponsePage.CONFLICT_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.INDEX_PAGE;

public class RegisterService {

    public HttpResponse getIndexOrConflictResponse(String account, String password, String email, Protocol protocol) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return HttpResponse.getCookieNullResponseEntity(protocol, CONFLICT_PAGE);
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponse.getCookieNullResponseEntity(protocol, INDEX_PAGE);
    }

}
