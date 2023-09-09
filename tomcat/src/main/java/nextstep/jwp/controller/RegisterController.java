package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.response.HttpResponseHeader.LOCATION;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.QueryStringUtil;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RegisterController extends AbstractController {

    private static final String PATH = "/register";
    private static final String INDEX_PAGE_PATH = "/index.html";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return PATH.equals(httpRequest.getPath());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        User user = extractUser(request.getBody());
        InMemoryUserRepository.save(user);

        response.setStatusCode(StatusCode.FOUND);
        response.addHeader(LOCATION, INDEX_PAGE_PATH);
    }

    private User extractUser(final String body) {
        Map<String, String> userData = QueryStringUtil.parse(body);
        return new User(
                userData.get("account"),
                userData.get("password"),
                userData.get("email")
        );
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String body = ViewResolver.findView(request.getPath().substring(1));
        response.setStatusCode(StatusCode.OK);
        response.setBody(body);
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getContentType());
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }
}
