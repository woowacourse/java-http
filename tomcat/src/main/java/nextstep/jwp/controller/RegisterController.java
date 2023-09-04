package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RegisterController implements Controller {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return "/register".equals(httpRequest.getPath())
                && HttpMethod.POST == httpRequest.getHttpMethod();
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        User user = extractUser(httpRequest.getBody());
        InMemoryUserRepository.save(user);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", "/index.html");
        return new HttpResponse(
                "HTTP/1.1",
                StatusCode.FOUND,
                headers
        );
    }

    private User extractUser(final String body) {
        Map<String, String> userData = new HashMap<>();
        final String[] split = body.split("&");
        for (String each : split) {
            final String[] keyAndValue = each.split("=");
            userData.put(keyAndValue[0], keyAndValue[1]);
        }
        return new User(
                userData.get("account"),
                userData.get("password"),
                userData.get("email")
                );
    }
}
