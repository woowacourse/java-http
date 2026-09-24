package org.apache.coyote.http11;

public class RequestMapping {
    private final StaticResourceHandler staticResourceHandler = new StaticResourceHandler();

    public Controller getController(HttpRequest request) {
        if ("/login".equals(request.getPathUri())) {
            return new LoginController(staticResourceHandler);
        }

        if ("/logout".equals(request.getPathUri())){
            return new LogoutController();
        }

        if ("/register".equals(request.getPathUri())){
            return new RegisterController(staticResourceHandler);
        }

        return new StaticResourceController(staticResourceHandler);
    }
}
