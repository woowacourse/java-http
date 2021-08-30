package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.util.ViewResolver;

public class GeneralResponse {

    private final HttpRequest httpRequest;

    public GeneralResponse(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String getResponse() throws IOException {
        return buildResponse(httpRequest.getRequestURLWithoutQuery());
    }

    private String buildResponse(String path) throws IOException {
        final ViewResolver viewResolver = new ViewResolver(path);
        final String responseBody = viewResolver.staticValue("html");

        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

}
