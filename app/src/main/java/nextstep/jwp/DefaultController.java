package nextstep.jwp;

import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;
import nextstep.jwp.network.HttpStatus;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultController extends AbstractController {

    public DefaultController(String resource) {
        super(resource);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        if ("/index.html".equals(httpRequest.toURI().getPath())) {
            URL resource = getClass().getClassLoader().getResource("static" + httpRequest.toURI().getPath());
            Path path = Paths.get(resource.getPath());
            byte[] bytes = Files.readAllBytes(path);

            return new HttpResponse(HttpStatus.OK, bytes);
        }

        byte[] bytes = "Hello world!".getBytes();
        return new HttpResponse(HttpStatus.OK, bytes);
    }
}
