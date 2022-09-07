package nextstep.jwp.controller;

import java.nio.file.Files;
import org.apache.coyote.StaticFile;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticController extends Controller {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.path();
        String body = new String(Files.readAllBytes(StaticFile.findByPath(path)
                .toPath()));
        response.setHeader("Content-Type", ContentType.from(path).value());
        response.setHeader("Content-Length", body.getBytes().length);
        response.body(body);
    }
}
