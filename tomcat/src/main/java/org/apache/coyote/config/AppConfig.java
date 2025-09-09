package org.apache.coyote.config;

import org.apache.coyote.render.PageRenderer;
import org.apache.coyote.handler.UserLoginHandler;
import org.apache.coyote.handler.UserRegisterHandler;
import org.apache.coyote.router.RequestRouter;

public class AppConfig {

    private static AppConfig instance;
    private final RequestRouter requestRouter;

    private AppConfig() {
        this.requestRouter = createRequestRouter();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public RequestRouter getRequestRouter() {
        return requestRouter;
    }

    private RequestRouter createRequestRouter() {
        return new RequestRouter(new UserLoginHandler(), new UserRegisterHandler(), new PageRenderer());
    }
}
