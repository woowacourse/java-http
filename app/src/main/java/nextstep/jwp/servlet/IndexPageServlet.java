package nextstep.jwp.servlet;

import java.io.IOException;

public class IndexPageServlet extends Servlet {
    private static String INDEX_PAGE_PATH = "static/index.html";

    @Override
    public void response() throws IOException {
        final String responseBody = readFile(INDEX_PAGE_PATH);

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
