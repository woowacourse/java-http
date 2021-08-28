package nextstep.jwp;

public class URI {

    private final String value;

    // https://www.naver.com/sports/soccer?name=messi
    // schema
    // authority
    // path
    // query
    // fragment

    public URI(String value) {
        this.value = value;
    }

    public String getPath() {
        if (hasQuery()) {
            final int queryStart = value.indexOf("?");
            return value.substring(0, queryStart);
        }
        return value;
    }

    public boolean hasQuery() {
        return value.contains("?");
    }

    public String getQuery() {
        final int queryStart = value.indexOf("?");
        return value.substring(queryStart + 1);
    }
}
