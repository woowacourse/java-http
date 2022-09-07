package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.HttpMethod.GET;

import java.io.IOException;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FileController extends AbstractController {

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.hasHttpMethodOf(GET) && httpRequest.isFileRequest();
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        super.readFile(httpRequest.getPath(), httpResponse);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new ResourceNotFoundException();
    }
}
