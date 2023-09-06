package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

import static org.apache.coyote.http11.common.HttpStatus.FOUND;

public class RegisterHandler {

    private RegisterHandler() {
    }

    public static HttpResponse handle(final RequestLine requestLine, final RequestHeader requestHeader, final RequestBody requestBody) {
        if (requestLine.getRequestMethod().isSameRequestMethod("GET")) {
            return StaticFileHandler.handle("/register.html",requestHeader);
        }
        return register(requestBody);
    }

    private static HttpResponse register(final RequestBody requestBody) {
        if (requestBody == null) {
            return new HttpResponseBuilder().init()
                    .httpStatus(FOUND)
                    .header("Location: 401.html")
                    .build();
        }
        String account = requestBody.getContentValue("account");
        String email = requestBody.getContentValue("email");
        String password = requestBody.getContentValue("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return new HttpResponseBuilder().init()
                .httpStatus(FOUND)
                .header("Location: /index.html")
                .build();
    }
}
