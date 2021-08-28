package nextstep.jwp.http.request;

public class RequestBody {

    private static final RequestBody EMPTY = new RequestBody(null);
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final String value;

    public RequestBody(String value) {
        this.value = value;
    }

    public static RequestBody empty() {
        return EMPTY;
    }

    public String getParameter(String parameter) {
        return null;
    }

    @Override
    public String toString() {
        return NEW_LINE + value;
    }
}
