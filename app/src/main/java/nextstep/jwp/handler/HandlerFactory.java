package nextstep.jwp.handler;

public class HandlerFactory {

    private HandlerFactory() {
    }

    public static Handler handler(String path) {
        if ("/".equals(path)) {
            return new BaseHandler();
        }
        if (path.contains("/index")) {
            return new IndexHandler();
        }
        if (path.contains("/login")) {
            return new LoginHandler();
        }
        if (path.contains("/register")) {
            return new RegisterHandler();
        }
        if (path.contains(".css")) {
            return new CSSHandler();
        }
        if (path.contains(".js")) {
            return new JSHandler();
        }
        return new NotFoundHandler();
    }
}
