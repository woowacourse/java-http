package nextstep.jwp.response;

import java.io.IOException;
import nextstep.jwp.util.TranslatedFile;

public class RegisterResponse {

    private static final String REGISTER = "/register";
    private static final String SUCCESS = "/index";
    private static final String FAIL = "/401";

    public RegisterResponse() {

    }

    public String successResponse() throws IOException {
        final TranslatedFile translatedFile = new TranslatedFile(REGISTER);
        final String responseBody = translatedFile.staticValue("html");
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    public String createAccountResponse() throws IOException {
        final TranslatedFile translatedFile = new TranslatedFile(SUCCESS);
        final String responseBody = translatedFile.staticValue("html");
        return String.join("\r\n",
            "HTTP/1.1 302 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    public String failedResponse() throws IOException {
        final TranslatedFile translatedFile = new TranslatedFile(FAIL);
        final String responseBody = translatedFile.staticValue("html");

        return String.join("\r\n",
            "HTTP/1.1 401 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
