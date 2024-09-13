package org.apache.coyote.http11.handler;

import com.techcourse.controller.LoginGetController;
import com.techcourse.controller.LoginPostController;
import com.techcourse.controller.RegisterGetController;
import com.techcourse.controller.RegisterPostController;
import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.RequestMapperKey;

public class HttpHandlerMapperFactory {

    private HttpHandlerMapperFactory() {
    }

    public static HttpHandlerMapper create() {
        UserService userService = new UserService();

        return new HttpHandlerMapper(Map.of(
                new RequestMapperKey(HttpMethod.GET, "/"), new StringHttpHandler("Hello world!"),
                new RequestMapperKey(HttpMethod.GET, "/login"), new LoginGetController(),
                new RequestMapperKey(HttpMethod.POST, "/login"), new LoginPostController(userService),
                new RequestMapperKey(HttpMethod.GET, "/register"), new RegisterGetController(),
                new RequestMapperKey(HttpMethod.POST, "/register"), new RegisterPostController(userService)
        ));
    }
}
