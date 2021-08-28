package nextstep.jwp.servlet;

import java.io.IOException;

public class LoginPageServlet extends Servlet {
    private static String LOGIN_PAGE_PATH = "static/login.html";

    @Override
    public void response() throws IOException {
        final String responseBody = readFile(LOGIN_PAGE_PATH);

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
