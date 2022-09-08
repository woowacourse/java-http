package nextstep.jwp.model;

public class Path {

    private static final Path LOGIN_PAGE_URL = new Path("/login.html");
    private static final String LOGIN_PATH = "/login";
    private static final Path REGISTER_PAGE_URL = new Path("/register.html");
    private static final String REGISTER_PATH = "/register";

    private final String path;

    public Path(final String path) {
        this.path = path;
    }

    public static Path fromUri(final String uri) {
        final String path = uri.split("\\?")[0];
        if (path.equals(LOGIN_PATH)) {
            return LOGIN_PAGE_URL;
        }
        if (path.equals(REGISTER_PATH)) {
            return REGISTER_PAGE_URL;
        }
        return new Path(path);
    }

    public String get() {
        return path;
    }
}
