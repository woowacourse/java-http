package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.util.HttpStatus;
import nextstep.jwp.util.StaticResources;
import nextstep.jwp.util.ViewResolver;

public class RegisterResponse {

    private static final String REGISTER = "/register";
    private static final String SUCCESS = "/index";
    private static final String FAIL = "/401";

    public String successResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(REGISTER);
        final StaticResources staticResource = StaticResources.basicType();
        final String responseBody = viewResolver.staticValue(staticResource.resource());
        String firstLine = String.join(" ","HTTP/1.1", HttpStatus.OK.value(),
            HttpStatus.OK.method());
        return String.join("\r\n",
            firstLine,
            "Content-Type:" + staticResource.type(),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    public String createAccountResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(SUCCESS);
        final StaticResources staticResource = StaticResources.basicType();
        final String responseBody = viewResolver.staticValue(staticResource.resource());
        return responseOnTryingRegister(staticResource, responseBody, HttpStatus.FOUND,
            "Location: /index");
    }

    public String failedResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(FAIL);
        final StaticResources staticResource = StaticResources.basicType();
        final String responseBody = viewResolver.staticValue(staticResource.resource());
        return responseOnTryingRegister(staticResource, responseBody, HttpStatus.UNAUTHORIZED,
            "Location: /401");
    }

    private String responseOnTryingRegister(StaticResources staticResource, String responseBody,
        HttpStatus unauthorized, String s) {
        String firstLine = String.join(" ", "HTTP/1.1", unauthorized.value(),
            unauthorized.method());
        return String.join("\r\n",
            firstLine,
            s,
            "Content-Type:" + staticResource.type(),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
