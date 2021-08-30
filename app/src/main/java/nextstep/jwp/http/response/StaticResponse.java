package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.util.ViewResolver;

public class StaticResponse {

    private final String resourceType;
    private final String path;

    public StaticResponse(String resourceType, String path) {
        this.resourceType = resourceType;
        this.path = path;
    }

    public String response() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(path);
        final String responseBody = viewResolver.staticValue(resourceType);
        return String.join("\r\n",
            "HTTP/1.1 401 OK ",
            "Content-Type: text/",
            resourceType,
            ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
