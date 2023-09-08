package org.apache.coyote.http11.auth;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.response.ResponsePage.CONFLICT_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.INDEX_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.REGISTER_PAGE;

public class RegisterService {

    public ResponseEntity register(final RequestLine requestLine, final RequestBody requestBody) {
        Protocol protocol = requestLine.protocol();
        if (requestLine.method() == GET) {
            return ResponseEntity.getCookieNullResponseEntity(protocol, REGISTER_PAGE);
        }
        return getIndexOrConflictResponse(requestBody, protocol);

    }

    private static ResponseEntity getIndexOrConflictResponse(RequestBody requestBody, Protocol protocol) {
        final String account = requestBody.getBy("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return ResponseEntity.getCookieNullResponseEntity(protocol, CONFLICT_PAGE);
        }

        final String email = requestBody.getBy("email");
        final String password = requestBody.getBy("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return ResponseEntity.getCookieNullResponseEntity(protocol, INDEX_PAGE);
    }

}
