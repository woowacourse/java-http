package com.techcourse.web.router;

import com.techcourse.web.controller.common.Controller;
import com.techcourse.web.controller.HomeController;
import com.techcourse.web.controller.LoginController;
import com.techcourse.web.controller.RegisterController;
import com.techcourse.web.controller.common.StaticFileResolver;
import com.techcourse.web.request.AppRequest;
import com.techcourse.web.view.AppResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class AppRouter {

    private static final AppRouter INSTANCE = new AppRouter();

    private final Map<String, Controller> routes = new HashMap<>();

    public AppRouter() {
        initializeRoutes();
    }

    public static AppRouter getInstance() {
        return INSTANCE;
    }
    
    private void initializeRoutes() {
        routes.put("/", HomeController.getInstance());
        routes.put("/login", LoginController.getInstance());
        routes.put("/register", RegisterController.getInstance());
    }

    public AppResponse route(final AppRequest request) throws Exception {
        final Controller controller = routes.get(request.getPath());

        if (controller == null) {
            return StaticFileResolver.resolve(request);
        }

        return controller.service(request);
    }
}
