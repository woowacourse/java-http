package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseFactory;
import org.apache.coyote.http11.HttpResponseStatusLine;

public class RegisterController extends RestController {

    private static final String SUPPORTED_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String INDEX_PAGE = "/index.html";

    public RegisterController() {
        super("/register");
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        final Map<String, String> body = httpRequest.getBody();
        final User user = new User(body.get("account"), body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);

        return HttpResponseFactory.createRedirectHttpResponse(HttpResponseStatusLine.FOUND(), INDEX_PAGE);
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final boolean isPostMethod = httpRequest.getMethod() == HttpMethod.POST;
        final boolean isSupportedContentType = httpRequest.containsContentType(SUPPORTED_CONTENT_TYPE);

        return super.canHandle(httpRequest) && isPostMethod && isSupportedContentType;
    }
}
