package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.ContentType;
import nextstep.jwp.FileReader;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class HomeController extends AbstractController {

    private static final String ROOT_PAGE = "/";

    public HomeController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public byte[] get(HttpRequest httpRequest) throws IOException {
        if (ROOT_PAGE.equals(httpRequest.uri())) {
            return HttpResponse.ok(
                    FileReader.file(Controller.INDEX_PAGE),
                    ContentType.findBy(Controller.INDEX_PAGE)
            );
        }
        return HttpResponse.ok(
                FileReader.file(httpRequest.uri()),
                ContentType.findBy(httpRequest.uri())
        );
    }

    @Override
    public byte[] post(HttpRequest httpRequest) throws IOException {
        return HttpResponse.error(HttpError.FORBIDDEN);
    }

    @Override
    public byte[] error(HttpError httpError) throws IOException {
        return HttpResponse.error(HttpError.FORBIDDEN);
    }
}
