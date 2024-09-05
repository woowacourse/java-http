package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Response.Http11ResponseBuilder;

public class LoginRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) throws IOException {
        return httpRequest.getPath().equals("/login")
                && httpRequest.existsQueryParam()
                && "GET".equals(httpRequest.getMethod());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        Http11ResponseBuilder responseBuilder = Http11Response.builder()
                .protocol(httpRequest.getVersionOfProtocol());
        String account = httpRequest.getQueryParam().get("account");
        String password = httpRequest.getQueryParam().get("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        String redirectPath = "/401.html";
        if (user.isPresent() && user.get().checkPassword(password)) {
            redirectPath = "/index.html";
        }
        return responseBuilder
                .statusCode(302)
                .statusMessage("Found")
                .appendHeader("Location", redirectPath)
                .build();
    }
}
