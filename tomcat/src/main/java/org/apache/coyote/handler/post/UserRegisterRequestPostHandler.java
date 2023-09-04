package org.apache.coyote.handler.post;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.Headers;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;

import java.util.Map;

import static org.apache.coyote.common.CharacterSet.UTF_8;
import static org.apache.coyote.common.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.common.HeaderType.CONTENT_TYPE;
import static org.apache.coyote.common.HeaderType.LOCATION;
import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.common.MediaType.TEXT_HTML;
import static org.apache.coyote.response.HttpStatus.FOUND;

public class UserRegisterRequestPostHandler implements RequestHandler {

    private static final String REGISTER_SUCCESS_REDIRECT_URI = "/index.html";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final RequestBody requestBody = httpRequest.requestBody();
        final String account = requestBody.getBodyValue("account");
        final String password = requestBody.getBodyValue("password");
        final String email = requestBody.getBodyValue("email");

        InMemoryUserRepository.save(new User(account, password, email));

        final Headers homePageResponseHeader = new Headers(Map.of(
                CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source(),
                CONTENT_LENGTH.source(), String.valueOf(ResponseBody.EMPTY.length()),
                LOCATION.source(), REGISTER_SUCCESS_REDIRECT_URI)
        );

        return new HttpResponse(HTTP_1_1, FOUND, homePageResponseHeader, ResponseBody.EMPTY);
    }
}
