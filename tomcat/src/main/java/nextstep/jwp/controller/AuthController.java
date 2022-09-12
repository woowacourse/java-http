package nextstep.jwp.controller;

import static org.apache.coyote.page.PageMapper.getFilePath;

import java.util.NoSuchElementException;
import nextstep.jwp.exception.UnauthorizedUserException;
import nextstep.jwp.service.AuthService;
import nextstep.jwp.service.dto.UserResponseDto;
import org.apache.coyote.annotation.Controller;
import org.apache.coyote.annotation.RequestMapping;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

@Controller
public class AuthController {

    private final AuthService authService = new AuthService();

    @RequestMapping(value = "/login", httpMethod = HttpMethod.GET)
    public HttpResponse loginPage(final HttpRequest httpRequest) {
        if (httpRequest.getSession().isLoggedInUser()) {
            return redirectToIndex(httpRequest);
        }
        return HttpResponse.ok()
                .body(getFilePath(httpRequest.getUri()))
                .build();
    }

    @RequestMapping(value = "/login", httpMethod = HttpMethod.POST)
    public HttpResponse login(final HttpRequest httpRequest) {
        try {
            authService.login(httpRequest);
        } catch (UnauthorizedUserException | NoSuchElementException e) {
            return HttpResponse.redirect("/401.html")
                    .build();
        }
        return HttpResponse.redirect("/index.html")
                .addCookie(httpRequest.getCookie(), httpRequest.getSession())
                .build();
    }

    @RequestMapping(value = "/register", httpMethod = HttpMethod.GET)
    public HttpResponse registerPage(final HttpRequest httpRequest) {
        return HttpResponse.ok()
                .body(getFilePath(httpRequest.getUri()))
                .build();
    }

    @RequestMapping(value = "/register", httpMethod = HttpMethod.POST)
    public HttpResponse register(final HttpRequest httpRequest) {
        final UserResponseDto result = authService.register(httpRequest);
        return HttpResponse.created(result.getId(), "/index.html")
                .build();
    }

    private HttpResponse redirectToIndex(HttpRequest httpRequest) {
        httpRequest.getSession().changeLastAccessedTime();
        return HttpResponse.redirect("/index.html")
                .addCookie(httpRequest.getCookie(), httpRequest.getSession())
                .build();
    }

}
