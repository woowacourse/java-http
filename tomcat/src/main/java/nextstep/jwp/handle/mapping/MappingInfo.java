package nextstep.jwp.handle.mapping;

import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.request.HttpRequest;

public abstract class MappingInfo {

    protected final HttpMethod httpMethod;
    protected final String uriPath;

    protected MappingInfo(final HttpMethod httpMethod, final String uriPath) {
        this.httpMethod = httpMethod;
        this.uriPath = uriPath;
    }

    public abstract boolean support(final HttpRequest request);
}
