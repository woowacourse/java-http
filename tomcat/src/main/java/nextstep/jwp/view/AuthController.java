package nextstep.jwp.view;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.http.BasicHttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.info.HttpMethod;
import org.apache.http.info.HttpVersion;
import org.apache.http.info.StatusCode;
import org.springframework.annotation.Controller;
import org.springframework.annotation.RequestMapping;
import org.springframework.http.Cookie;

@Controller
public class AuthController {

    @RequestMapping(method = HttpMethod.POST, uri = "/login")
    public HttpResponse login(final HttpRequest httpRequest) {
        final Map<String, String> body = httpRequest.getBody();
        final var account = body.getOrDefault("account", "");
        final var password = body.getOrDefault("password", "");

        final Optional<User> foundUser = InMemoryUserRepository.findByAccountAndPassword(account, password);

        final Cookie cookie = Cookie.builder()
                .name("JSESSIONID")
                .value(UUID.randomUUID().toString())
                .build();

        return BasicHttpResponse.builder()
                .httpVersion(HttpVersion.HTTP_1_1)
                .statusCode(StatusCode.FOUND_302)
                .setCookie(cookie)
                .addHeader("Location", "index.html")
                .build();
    }
}
