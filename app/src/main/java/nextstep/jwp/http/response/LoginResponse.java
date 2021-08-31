package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.util.HttpStatus;
import nextstep.jwp.util.StaticResources;
import nextstep.jwp.util.ViewResolver;

public class LoginResponse {

    private static final String SUCCESS = "/index";
    private static final String FAIL = "/401";

    public String successResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(SUCCESS);
        final StaticResources staticResource = StaticResources.basicType();
        final String responseBody = viewResolver.staticValue(staticResource.resource());
        String firstLine = String.join(" ","HTTP/1.1", HttpStatus.FOUND.value(),
            HttpStatus.FOUND.method());
        return String.join("\r\n",
            firstLine,
            "Location: /index",
            "Content-Type:" + staticResource.type(),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    public String failedResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(FAIL);
        final StaticResources staticResource = StaticResources.basicType();
        final String responseBody = viewResolver.staticValue(staticResource.resource());
        String firstLine = String.join(" ","HTTP/1.1", HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.method());
        return String.join("\r\n",
            firstLine,
            "Location: /401",
            "Content-Type:" + staticResource.type(),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
