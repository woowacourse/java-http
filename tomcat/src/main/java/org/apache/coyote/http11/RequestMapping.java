package org.apache.coyote.http11;

public class RequestMapping {

    private final HomeController homeController;
    private final LoginController loginController;
    private final RegisterController registerController;
    private final ResourceController resourceController;

    public RequestMapping(
            HomeController homeController,
            LoginController loginController,
            RegisterController registerController,
            ResourceController resourceController
    ) {
        this.homeController = homeController;
        this.loginController = loginController;
        this.registerController = registerController;
        this.resourceController = resourceController;
    }

    public Controller getController(HttpRequest request) {
        String uri = request.uri();
        if (uri.equals("/")) {
            return homeController;
        }
        if (uri.equals("/login")) {
            return loginController;
        }
        if (uri.equals("/register")) {
            return registerController;
        }
        return resourceController;
    }
}
