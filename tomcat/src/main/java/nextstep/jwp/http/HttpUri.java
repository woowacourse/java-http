package nextstep.jwp.http;

public class HttpUri {

    private final String nativePath;
    private final QueryString queryString;

    private HttpUri(String nativePath, QueryString queryString) {
        this.nativePath = nativePath;
        this.queryString = queryString;
    }

    public static HttpUri from(String fullPath) {
        int index = fullPath.indexOf("?");

        if (index == -1) {
            return new HttpUri(fullPath, QueryString.from(""));
        }

        String nativePath = fullPath.substring(0, index);
        QueryString queryString = QueryString.from(fullPath.substring(index + 1));
        return new HttpUri(nativePath, queryString);
    }

    public String getNativePath() {
        return nativePath;
    }
}
