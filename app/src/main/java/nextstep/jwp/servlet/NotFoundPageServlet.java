package nextstep.jwp.servlet;

import java.io.IOException;

public class NotFoundPageServlet extends Servlet {
    private static String ERROR_PAGE_PATH = "static/404.html";

    @Override
    public void response() throws IOException {
        final String responseBody = readFile(ERROR_PAGE_PATH);

        final String response = String.join("\r\n",
                "HTTP/1.1 404 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
