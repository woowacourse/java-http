package nextstep.jwp.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.tomcat.Servlet;

public class StaticResourceServlet extends Servlet {

    public StaticResourceServlet() {
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String fileName = httpRequest.getUri();
        URL resource = getClass().getResource("/static/" + fileName);

        if (Objects.isNull(resource)) {
            resource = getClass().getResource("/static/" + fileName + ".html");
        }

        httpResponse.addStartLine("HTTP/1.1", "200", "OK");
        addContentType(fileName, httpResponse);
        httpResponse.addBody(readFile(resource));
    }

    private String readFile(URL resource) throws IOException {
        Path path = new File(resource.getPath()).toPath();
        List<String> file = Files.readAllLines(path);
        String responseBody = String.join("\r\n", file);
        return responseBody;
    }

    private void addContentType(String fileName, HttpResponse httpResponse) {
        if (fileName.endsWith(".css")) {
            httpResponse.addContentType("text/css,*/*;q=0.1");
        } else if (fileName.endsWith(".js")) {
            httpResponse.addContentType("text/javascript");
        } else {
            httpResponse.addContentType("text/html;charset=utf-8");
        }
    }

}
