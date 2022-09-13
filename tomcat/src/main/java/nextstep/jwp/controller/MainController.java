package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Status;

public class MainController implements Controller {

    @Override
    public boolean isUrlMatches(final String url) {
        return false;
    }

    @Override
    public HttpResponse doGet(final HttpRequest request) throws IOException {
        HttpResponse response = HttpResponse.of(Status.OK);
        String url = request.getUrl();
        if (url.equals("/")) {
            url = "/hello.txt";
        }
        response.addResource(ResourceLoader.load(url));
        return response;
    }

    @Override
    public HttpResponse doPost(final HttpRequest request) throws IOException {
        return null;
    }
}
