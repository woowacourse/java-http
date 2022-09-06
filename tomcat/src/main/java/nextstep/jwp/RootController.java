package nextstep.jwp;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Resource;
import org.apache.coyote.http11.request.ResourceLocator;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;

public class RootController extends AbstractController {

    public RootController(ResourceLocator resourceLocator) {
        super(resourceLocator);
    }

    @Override
    public boolean isProcessable(HttpRequest request) {
        return request.isPathEqualTo("/");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            Resource resource = resourceLocator.locate("/hello.html");

            response.setStatus(HttpStatus.OK);
            response.addHeader("Content-Type", resource.getMimeType().getValue());
            response.setBody(resource.getData());
        } catch (IllegalArgumentException e) {
            Resource resource = resourceLocator.locate("/404.html");
            response.setStatus(HttpStatus.NOT_FOUND);
            response.addHeader("Content-Type", resource.getMimeType().getValue());
            response.setBody(resource.getData());
        }
    }

}
