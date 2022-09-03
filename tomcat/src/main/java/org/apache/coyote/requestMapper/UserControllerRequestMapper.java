package org.apache.coyote.requestMapper;

import nextstep.jwp.controller.UserController;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class UserControllerRequestMapper implements RequestMapper {

    private UserController controller = new UserController();

    @Override
    public HttpResponse mapping(final HttpRequest httpRequest) {
        if(httpRequest.getUrl().startsWith("/login")){
            return controller.login(httpRequest);
        }
        return HttpResponse.notFound().build();
    }
}
