package nextstep.jwp.web.handler;

import java.util.List;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.resolver.UriResolver;
import nextstep.jwp.web.resolver.StaticResourceResolver;
import nextstep.jwp.web.resolver.ViewResolver;

public class WebStatusHandler implements WebHandler {

    private final List<ViewResolver> viewResolvers;

    public WebStatusHandler() {
        this(List.of(new UriResolver(), new StaticResourceResolver()));
    }

    public WebStatusHandler(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    @Override
    public void doHandle(HttpRequest request, HttpResponse response) {
        resolveData(response);
    }

    private void resolveData(HttpResponse response) {
        for (ViewResolver viewResolver : viewResolvers) {
            if (viewResolver.isSuitable(response)
            ) {
                viewResolver.resolve(response);
                break;
            }
        }
    }

}
