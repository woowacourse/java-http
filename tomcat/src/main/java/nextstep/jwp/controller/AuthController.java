package nextstep.jwp.controller;

import static org.apache.coyote.page.PageMapper.getFilePath;

import java.util.NoSuchElementException;
import nextstep.jwp.exception.UnauthorizedUserException;
import nextstep.jwp.service.AuthService;
import org.apache.coyote.annotation.RequestMapping;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.annotation.Controller;

@Controller
public class AuthController {

    private final AuthService authService = new AuthService();

    @RequestMapping(value = "/login", httpMethod = HttpMethod.GET)
    public HttpResponse loginPage(final HttpRequest httpRequest){
        return HttpResponse.ok()
                .body(getFilePath(httpRequest.getUri()))
                .build();
    }

    @RequestMapping(value = "/login", httpMethod = HttpMethod.POST)
    public HttpResponse login(final HttpRequest httpRequest){
        try{
            authService.login(httpRequest);
        } catch (UnauthorizedUserException | NoSuchElementException e){
            return HttpResponse.redirect("/401.html").build();
        }
        return HttpResponse.redirect("/index.html").build();
    }

}
