package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.util.HttpStatus;
import nextstep.jwp.util.ViewResolver;

public class RegisterResponse {

    private static final String REGISTER = "/register";
    private static final String SUCCESS = "/index";
    private static final String FAIL = "/401";

    public RegisterResponse() {

    }

    public String successResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(REGISTER);
        final String responseBody = viewResolver.staticValue("html");
        String firstLine = String.join(" ","HTTP/1.1", HttpStatus.OK.value(),
            HttpStatus.OK.method());
        return String.join("\r\n",
            firstLine,
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    public String createAccountResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(SUCCESS);
        final String responseBody = viewResolver.staticValue("html");
        String firstLine = String.join(" ","HTTP/1.1", HttpStatus.FOUND.value(),
            HttpStatus.FOUND.method());
        return String.join("\r\n",
            firstLine,
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    public String failedResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(FAIL);
        final String responseBody = viewResolver.staticValue("html");
        String firstLine = String.join(" ","HTTP/1.1", HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.method());
        return String.join("\r\n",
            firstLine,
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
