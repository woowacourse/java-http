package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(StaticResourceController.load("/register"));
        SessionSupport.addCookie(response, SessionSupport.from(request));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> parameters = parseForm(request.getBody());
        InMemoryUserRepository.save(new User(
                parameters.get("account"),
                parameters.get("password"),
                parameters.get("email")
        ));
        response.setStatus(302);
        response.setHeader("Location", "/index.html");
        SessionSupport.addCookie(response, SessionSupport.from(request));
    }

    private Map<String, String> parseForm(final String body) {
        final Map<String, String> parameters = new HashMap<>();
        for (final String parameter : body.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length == 2) {
                parameters.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return parameters;
    }
}
