package nextstep.jwp.http.request.body;

public class Body {

    private static final Body EMPTY = new Body(null);

    private final String value;

    public Body(String value) {
        this.value = value;
    }

    public static Body empty() {
        return EMPTY;
    }
}
