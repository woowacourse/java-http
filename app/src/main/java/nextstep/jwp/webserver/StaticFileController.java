package nextstep.jwp.webserver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticFileController implements Controller {

    @Override
    public HttpResponse handle(HttpRequest request) {
        String body = respondStaticFile(request.getUri());

        return new HttpResponse(body);
    }

    private String respondStaticFile(String uriPath) {
        String[] paths = uriPath.split("/");
        String fileName = paths[paths.length - 1];
        URL resource = getClass().getClassLoader().getResource("static/" + fileName);

        if (resource == null) {
            throw new RuntimeException(); // 파일없음
        }
        Path path = new File(resource.getPath()).toPath();

        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "파일 read 중 에러 발생";
        }
    }

}
