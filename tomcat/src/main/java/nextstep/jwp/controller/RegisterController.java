package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.HttpResponseHeader.LOCATION;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.QueryStringUtil;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RegisterController implements Controller {

    private static final String PATH = "/register";
    private static final String INDEX_PAGE_PATH = "/index.html";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return PATH.equals(httpRequest.getPath())
                && HttpMethod.POST == httpRequest.getHttpMethod();
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        User user = extractUser(httpRequest.getBody());
        InMemoryUserRepository.save(user);

        final HttpResponse httpResponse = new HttpResponse(StatusCode.FOUND);
        httpResponse.addHeader(LOCATION, INDEX_PAGE_PATH);
        return httpResponse;
    }

    private User extractUser(final String body) {
        Map<String, String> userData = QueryStringUtil.parse(body);
        return new User(
                userData.get("account"),
                userData.get("password"),
                userData.get("email")
        );
    }
}
