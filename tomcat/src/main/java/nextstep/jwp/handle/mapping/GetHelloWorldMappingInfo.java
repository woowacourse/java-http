package nextstep.jwp.handle.mapping;

import java.util.Objects;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.request.HttpRequest;

public class GetHelloWorldMappingInfo extends MappingInfo {

    public GetHelloWorldMappingInfo() {
        super(HttpMethod.GET, "/");
    }

    @Override
    public boolean support(final HttpRequest request) {
        return httpMethod.equals(request.getHttpMethod()) && Objects.equals(uriPath, request.getUriPath());
    }
}
