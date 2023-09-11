package nextstep.jwp.controller;

import static common.ResponseStatus.OK;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String resourceUrl = httpRequest.requestUri();
        ResourceManager manager = ResourceManager.from(resourceUrl);
        String resourceType = manager.extractResourceType();

        validateResourceType(resourceType);

        httpResponse.setResponseResource(
                OK,
                resourceType,
                manager.readResourceContent()
        );
    }

    private void validateResourceType(String resourceType) {
        if (resourceType.equals("html")) {
            throw new NotFoundException();
        }
    }
}
