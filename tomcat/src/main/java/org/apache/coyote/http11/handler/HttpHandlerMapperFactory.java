package org.apache.coyote.http11.handler;

import com.techcourse.controller.LoginGetController;
import com.techcourse.controller.LoginPostController;
import com.techcourse.controller.RegisterGetController;
import com.techcourse.controller.RegisterPostController;
import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequestInfo;

public class HttpHandlerMapperFactory {

    private HttpHandlerMapperFactory() {
    }

    public static HttpHandlerMapper create() {
        UserService userService = new UserService();

        return new HttpHandlerMapper(Map.of(
                new HttpRequestInfo(HttpMethod.GET, "/"), new StringHttpHandler("Hello world!"),
                new HttpRequestInfo(HttpMethod.GET, "/login"), new LoginGetController(),
                new HttpRequestInfo(HttpMethod.POST, "/login"), new LoginPostController(userService),
                new HttpRequestInfo(HttpMethod.GET, "/register"), new RegisterGetController(),
                new HttpRequestInfo(HttpMethod.POST, "/register"), new RegisterPostController(userService)
        ));
    }
}
