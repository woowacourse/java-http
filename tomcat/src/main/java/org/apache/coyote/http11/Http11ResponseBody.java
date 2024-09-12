package org.apache.coyote.http11;

public class Http11ResponseBody {

    private final String body;

    private Http11ResponseBody(String body) {
        this.body = body;
    }

    public static Http11ResponseBody of(String response) {
        return new Http11ResponseBody(response);
    }

    public static Http11ResponseBody of() {
        return new Http11ResponseBody("");
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Http11ResponseBody{" +
                "body='" + body + '\'' +
                '}';
    }
}
