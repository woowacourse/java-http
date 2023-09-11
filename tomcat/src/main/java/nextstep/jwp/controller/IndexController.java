package nextstep.jwp.controller;

import static common.ResponseStatus.OK;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class IndexController extends AbstractController {

    private static final String URL = "/index";
    private static final String EXTENSION = ".html";

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return httpRequest.consistsOf(URL) || httpRequest.consistsOf(URL + EXTENSION);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String requestUrl = toRequestUrl(httpRequest);
        ResourceManager manager = ResourceManager.from(requestUrl);
        httpResponse.setResponseResource(
                OK,
                manager.extractResourceType(),
                manager.readResourceContent()
        );
    }

    private String toRequestUrl(HttpRequest httpRequest) {
        String location = httpRequest.requestUri();
        if (location.contains(EXTENSION)) {
            return location;
        }
        return location + EXTENSION;
    }
}
