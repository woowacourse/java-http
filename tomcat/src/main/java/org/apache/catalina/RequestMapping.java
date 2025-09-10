package org.apache.catalina;

import com.techcourse.controller.AuthController;
import com.techcourse.controller.Controller;
import com.techcourse.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {
    
    public Controller getController(HttpRequest request) {
        if (isAuthRequest(request)) {
            return new AuthController();
        } else {
            return new StaticResourceController();
        }
    }
    
    private boolean isAuthRequest(HttpRequest request) {
        String uri = request.getRequestUri();
        return "/login".equals(uri) || "/register".equals(uri);
    }
}
