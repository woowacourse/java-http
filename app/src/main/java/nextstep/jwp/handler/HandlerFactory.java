package nextstep.jwp.handler;

public class HandlerFactory {

    public static final String BASE_PATH = "/";
    public static final String INDEX_PATH = "/index";
    public static final String LOGIN_PATH = "/login";
    public static final String REGISTER_PATH = "/register";
    public static final String CSS_EXTENSION = ".css";
    public static final String JS_EXTENSION = ".js";

    private HandlerFactory() {
    }

    public static Handler handler(String path) {
        if (BASE_PATH.equals(path)) {
            return new BaseHandler();
        }
        if (path.contains(INDEX_PATH)) {
            return new IndexHandler();
        }
        if (path.contains(LOGIN_PATH)) {
            return new LoginHandler();
        }
        if (path.contains(REGISTER_PATH)) {
            return new RegisterHandler();
        }
        if (path.contains(CSS_EXTENSION)) {
            return new CSSHandler();
        }
        if (path.contains(JS_EXTENSION)) {
            return new JSHandler();
        }
        return new NotFoundHandler();
    }
}
