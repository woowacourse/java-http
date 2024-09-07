package org.apache.coyote.http11.handler;

import com.techcourse.controller.LoginController;
import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequestInfo;

public class HttpHandlerMapperFactory {

    private HttpHandlerMapperFactory() {
    }

    public static HttpHandlerMapper create() {
        return new HttpHandlerMapper(Map.of(
                new HttpRequestInfo(HttpMethod.GET, "/"), new StringHttpHandler("Hello world!"),
                new HttpRequestInfo(HttpMethod.GET, "/login"), new LoginController(new UserService())
        ));
    }
}
