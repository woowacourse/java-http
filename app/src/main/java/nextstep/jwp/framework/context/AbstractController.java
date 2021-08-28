package nextstep.jwp.framework.context;

import java.util.EnumSet;

import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;

public abstract class AbstractController implements Controller {

    protected final String mappingUri;

    protected final EnumSet<HttpMethod> handlingMethod;

    public AbstractController(String mappingUri, EnumSet<HttpMethod> handlingMethod) {
        this.mappingUri = mappingUri;
        this.handlingMethod = handlingMethod;
    }

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.isSameUri(mappingUri) && handlingMethod.contains(httpRequest.getMethod());
    }
}
