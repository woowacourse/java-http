package nextstep.jwp.handle.mapping;

import java.util.Objects;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.request.HttpRequest;

public class GetLoginMappingInfo extends MappingInfo {

    public GetLoginMappingInfo() {
        super(HttpMethod.GET, "/login");
    }

    @Override
    public boolean support(final HttpRequest request) {
        return httpMethod.equals(request.getHttpMethod()) && Objects.equals(uriPath, request.getUriPath());
    }
}
