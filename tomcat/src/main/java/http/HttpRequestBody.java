package http;

public class HttpRequestBody {
    private final String body;

    public HttpRequestBody(String body) {
        this.body = body;
    }

    public String getBody() {
        if (body == null) {
            return "";
        }
        return body;
    }
}
