package nextstep.jwp;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Resource;
import org.apache.coyote.http11.request.ResourceLocator;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;

public class LoginController extends AbstractController {

    public LoginController(ResourceLocator resourceLocator) {
        super(resourceLocator);
    }

    @Override
    public boolean isProcessable(HttpRequest request) {
        return request.isPathEqualTo("/login");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            Resource resource = resourceLocator.locate(request.getPathString());

            response.setHttpStatus(HttpStatus.OK);
            response.setMimeType(resource.getMimeType());
            response.setBody(resource.getData());
        } catch (IllegalArgumentException e) {
            Resource resource = resourceLocator.locate("/404.html");
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setMimeType(resource.getMimeType());
            response.setBody(resource.getData());
        }
    }


}
