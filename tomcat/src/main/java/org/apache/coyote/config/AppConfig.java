package org.apache.coyote.config;

import org.apache.coyote.render.PageRenderer;
import org.apache.coyote.handler.UserLoginHandler;
import org.apache.coyote.handler.UserRegisterHandler;
import org.apache.coyote.router.RequestRouter;

public class AppConfig {

    private final RequestRouter requestRouter;

    private AppConfig() {
        this.requestRouter = createRequestRouter();
    }

    private static class SingletonHolder {
        private static final AppConfig INSTANCE = new AppConfig();
    }

    public static AppConfig getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public RequestRouter getRequestRouter() {
        return requestRouter;
    }

    private RequestRouter createRequestRouter() {
        return new RequestRouter(
                new UserLoginHandler(),
                new UserRegisterHandler(),
                new PageRenderer()
        );
    }
}