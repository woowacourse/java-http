package support;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.RequestMapping;

import java.util.Map;

public final class RequestMappingFixtures {

    private RequestMappingFixtures() {
    }

    public static RequestMapping requestMapping() {
        return new RequestMapping(
                Map.of(
                        "/", new RootController(),
                        "/login", new LoginController(),
                        "/register", new RegisterController()
                ),
                new StaticResourceController()
        );
    }
}
