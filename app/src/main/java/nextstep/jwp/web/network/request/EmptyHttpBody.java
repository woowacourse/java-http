package nextstep.jwp.web.network.request;

public class EmptyHttpBody implements HttpBody {

    private static final EmptyHttpBody INSTANCE = new EmptyHttpBody();

    private EmptyHttpBody() {}

    public static HttpBody getInstance() {
        return INSTANCE;
    }

    @Override
    public String getAttribute(String key) {
        return null;
    }
}
