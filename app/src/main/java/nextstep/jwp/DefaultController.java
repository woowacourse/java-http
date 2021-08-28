package nextstep.jwp;

import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;
import nextstep.jwp.network.HttpStatus;

public class DefaultController extends AbstractController {

    public DefaultController(String resource) {
        super(resource);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        if ("/index.html".equals(httpRequest.getURI().getPath())) {
            final byte[] bytes = readIndex();
            return new HttpResponse(HttpStatus.OK, bytes);
        }

        byte[] bytes = "Hello world!".getBytes();
        return new HttpResponse(HttpStatus.OK, bytes);
    }
}
