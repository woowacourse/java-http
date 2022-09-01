package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class Controller {

    public  HttpResponse showIndex() {
        return HttpResponse.withStaticResource(new StaticResource("Hello world!", ContentType.TEXT_HTML));
    }

    public  HttpResponse showLogin(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getQueryString() != null) {
            final var parameters = httpRequest.parseQueryString();

            final var account = parameters.get("account");
            final var user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

            final var password = parameters.get("password");
            if (user.checkPassword(password)) {
                System.out.println("user = " + user);
            }
        }

        return HttpResponse.withStaticResource(StaticResource.path("/login.html"));
    }

    public  HttpResponse show(final HttpRequest httpRequest) throws IOException {
        final var path = httpRequest.getPath();
        return HttpResponse.withStaticResource(StaticResource.path(path));
    }
}
