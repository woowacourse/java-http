package nextstep.jwp.controller;

import static nextstep.jwp.views.RequestLoginUserOutput.printRequestLoginUser;
import static org.apache.coyote.page.PageMapper.getFilePath;

import org.apache.coyote.annotation.RequestMapping;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.annotation.Controller;

@Controller
public class UserController {

    @RequestMapping(value = "/login", httpMethod = HttpMethod.GET)
    public HttpResponse login(final HttpRequest httpRequest){
        printRequestLoginUser(httpRequest);
        return HttpResponse.ok()
                .body(getFilePath(httpRequest.getUrl()))
                .build();
    }


}
