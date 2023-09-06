package nextstep.jwp.controller;

import java.io.File;
import nextstep.jwp.FileFinder;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class indexController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalArgumentException();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        File file = FileFinder.get(request.getUri());
//        response.setBody(new String(Files.readAllBytes(file.toPath())));
//        response.setHttpStatus(HttpStatus.OK);
//        response.setBody("Hello world!");
    }
}
