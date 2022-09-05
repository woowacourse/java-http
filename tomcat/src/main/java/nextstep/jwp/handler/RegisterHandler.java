package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatusCode;

public class RegisterHandler {

    public HttpResponse register(final HttpRequest httpRequest) {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            return HttpResponse.of(httpRequest, HttpStatusCode.OK, "/register.html");
        }

        final HttpRequestBody requestBody = httpRequest.getBody();
        final User user = createUser(requestBody);
        InMemoryUserRepository.save(user);

        final HttpResponse response = HttpResponse.of(httpRequest, HttpStatusCode.FOUND, "/register.html");
        response.addHeader("Location", "/login.html");
        return response;
    }

    private User createUser(final HttpRequestBody requestBody) {
        final String account = requestBody.findByKey("account");
        final String password = requestBody.findByKey("password");
        final String email = requestBody.findByKey("email");
        return new User(account, password, email);
    }
}
