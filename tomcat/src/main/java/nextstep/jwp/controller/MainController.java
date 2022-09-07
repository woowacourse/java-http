package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Resource;
import org.apache.coyote.http11.model.response.Status;

public class MainController {

    public static HttpResponse hello() throws IOException {
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(findResource("/hello.txt"));
        return response;
    }

    public static HttpResponse index() throws IOException {
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(findResource("/index.html"));
        return response;
    }

    public static HttpResponse template(final HttpRequest request) throws IOException {
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(findResource(request.getUrl()));
        return response;
    }

    private static Resource findResource(final String url) throws IOException {
        Path path = Path.of(MainController.class.getResource("/static" + url).getPath());
        String body = Files.readString(path);

        ContentType contentType = ContentType.findByExtension(url);

        return new Resource(body, contentType);
    }
}
