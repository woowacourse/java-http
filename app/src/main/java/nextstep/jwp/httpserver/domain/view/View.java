package nextstep.jwp.httpserver.domain.view;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class View {
    private final String viewPath;

    public View(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(HttpResponse httpResponse) throws URISyntaxException, IOException {
        List<String> body;
        try {
            body = readFile(viewPath);
        } catch (NullPointerException e) {
            body = readFile("static/404.html");
        }

        responseBody(httpResponse, body);
    }

    private List<String> readFile(String filePath) throws URISyntaxException, IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(filePath);
        final Path path = Paths.get(url.toURI());
        return Files.readAllLines(path);
    }

    private void responseBody(HttpResponse httpResponse, List<String> body) throws IOException {
        final StringBuilder bodyBuilder = new StringBuilder();
        for (String bodyLine : body) {
            bodyBuilder.append(bodyLine).append("\r\n");
        }
        final String responseBody = bodyBuilder.toString();

        httpResponse.setBody(responseBody);

        final Path resourcePath = new File(viewPath).toPath();
        httpResponse.addHeader("Content-Type", Files.probeContentType(resourcePath) + ";charset=utf-8");
        httpResponse.addHeader("Content-Length", Integer.toString(responseBody.getBytes().length));
    }
}
