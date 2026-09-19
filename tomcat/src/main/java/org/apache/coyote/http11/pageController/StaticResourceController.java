package org.apache.coyote.http11.pageController;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.coyote.http11.StaticResourceLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class StaticResourceController extends AbstractController {
    private static final String NOT_FOUND_PAGE = "/404.html";

    private final StaticResourceLoader staticResourceLoader = new StaticResourceLoader();

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        try {
            return HttpResponse.of(HttpStatus.OK, staticResourceLoader.load(httpRequest.getHttpPath()));
        } catch (FileNotFoundException e) {
            return HttpResponse.of(HttpStatus.NOT_FOUND, staticResourceLoader.load(NOT_FOUND_PAGE));
        }
    }
}
