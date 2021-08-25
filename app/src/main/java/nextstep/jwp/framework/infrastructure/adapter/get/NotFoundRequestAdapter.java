package nextstep.jwp.framework.infrastructure.adapter.get;

import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class NotFoundRequestAdapter implements RequestAdapter {

    private final StaticFileResolver staticFileResolver;

    public NotFoundRequestAdapter(StaticFileResolver staticFileResolver) {
        this.staticFileResolver = staticFileResolver;
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        return staticFileResolver.renderDefaultViewByStatus(HttpStatus.NOT_FOUND);
    }
}
