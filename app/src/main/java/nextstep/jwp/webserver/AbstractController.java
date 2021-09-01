package nextstep.jwp.webserver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method = request.getHttpMethod();
        if (HttpMethod.GET == method) {
            doGet(request, response);
        }
        if (HttpMethod.POST == method) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected String readStaticFile(String fileName) {
        URL resource = getClass().getClassLoader().getResource("static/" + fileName);

        if (resource == null) {
            throw new RuntimeException();
        }
        Path path = new File(resource.getPath()).toPath();

        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "파일 read 중 에러 발생";
        }
    }

    protected String getContentType(String extension) {
        if ("js".equals(extension)) {
            return "text/javascript";
        }
        if ("css".equals(extension)) {
            return "text/css";
        }
        if ("html".equals(extension)) {
            return "text/html";
        }
        return "text/plain";
    }
}