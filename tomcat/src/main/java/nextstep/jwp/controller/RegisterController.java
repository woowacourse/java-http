package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.util.Map;

import static nextstep.jwp.controller.Path.MAIN;
import static nextstep.jwp.controller.Path.REGISTER;
import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class RegisterController extends RequestController {
    private static final String TARGET_URI = "register";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> bodyParams = request.getParsedBody();

        final String account = bodyParams.get("account");
        final String password = bodyParams.get("password");
        final String email = bodyParams.get("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.mapToRedirect(MAIN.getPath());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.changeStatusLine(StatusLine.from(StatusCode.OK));
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getValue());
        response.changeBody(HttpBody.file(REGISTER.getPath()));
    }
}
