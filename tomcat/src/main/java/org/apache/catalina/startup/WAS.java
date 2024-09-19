package org.apache.catalina.startup;

import com.techcourse.controller.HttpController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ResourceFinder;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class WAS {

    private static final LoginController LOGIN_CONTROLLER = new LoginController("/login");
    private static final RegisterController REGISTER_CONTROLLER = new RegisterController("/register");
    private static final Controllers controllers = new Controllers(
            LOGIN_CONTROLLER,
            REGISTER_CONTROLLER
    );

    private final Server tomcat;

    public WAS(Server tomcat) {
        this.tomcat = tomcat;
    }

    public void start() {
        tomcat.start();
    }

    public static HttpResponse dispatch(HttpRequest request) throws Exception {
        HttpResponse response = new HttpResponse();

        if (controllers.contains(request.getLocation())) {
            HttpController targetController = controllers.get(request.getLocation());
            targetController.service(request, response);
            return response;
        }
        ResourceFinder.setStaticResponse(request, response);

        return response;
    }
}
