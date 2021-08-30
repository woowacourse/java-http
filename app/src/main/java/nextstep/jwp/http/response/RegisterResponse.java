package nextstep.jwp.http.response;

import java.io.IOException;
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
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    public String createAccountResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(SUCCESS);
        final String responseBody = viewResolver.staticValue("html");
        return String.join("\r\n",
            "HTTP/1.1 302 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    public String failedResponse() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(FAIL);
        final String responseBody = viewResolver.staticValue("html");

        return String.join("\r\n",
            "HTTP/1.1 401 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
