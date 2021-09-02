package nextstep.jwp.web;

public class WebApplicationConfig {

    private WebApplicationConfig() {
    }

    public static FrontController frontController() {
        return new FrontController(requestMappingHandler());
    }

    public static RequestMappingHandler requestMappingHandler() {
        return new RequestMappingHandler(RequestMappingHandlerConfig.config());
    }
}
