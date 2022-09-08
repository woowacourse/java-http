package nextstep.jwp.view;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.http.BasicHttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.info.ContentType;
import org.apache.http.info.HttpMethod;
import org.apache.http.info.HttpVersion;
import org.apache.http.info.StatusCode;
import org.richard.utils.ResourceUtils;
import org.springframework.annotation.Controller;
import org.springframework.annotation.RequestMapping;

@Controller
public class UserController {

    @RequestMapping(method = HttpMethod.GET, uri = "/register")
    public HttpResponse registerPage(final HttpRequest httpRequest) {
        return BasicHttpResponse.builder()
                .httpVersion(HttpVersion.HTTP_1_1)
                .statusCode(StatusCode.OK_200)
                .contentType(ContentType.TEXT_HTML.getName())
                .body(ResourceUtils.createResourceAsString("static/register.html"))
                .build();
    }

    @RequestMapping(method = HttpMethod.POST, uri = "/register")
    public HttpResponse register(final HttpRequest httpRequest) {
        final var body = httpRequest.getBody();
        final var account = body.get("account");
        final var email = body.get("email");
        final var password = body.get("password");

        InMemoryUserRepository.save(new User(account, password, email));

        return BasicHttpResponse.builder()
                .httpVersion(HttpVersion.HTTP_1_1)
                .statusCode(StatusCode.FOUND_302)
                .addHeader("Location", "index.html")
                .build();
    }
}
