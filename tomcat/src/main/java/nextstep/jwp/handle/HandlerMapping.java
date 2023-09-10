package nextstep.jwp.handle;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.NotFoundHandlerException;
import nextstep.jwp.handle.mapping.MappingInfo;
import org.apache.coyote.request.HttpRequest;

public class HandlerMapping {

    private final Map<MappingInfo, HandlerMethod> handlerMapping = new HashMap<>();

    public HandlerMapping() {
    }

    public void addMappingInfo(final MappingInfo mappingInfo, final HandlerMethod handlerMethod) {
        handlerMapping.put(mappingInfo, handlerMethod);
    }

    public HandlerMethod getHandlerMethod(final HttpRequest request) {
        return handlerMapping.keySet()
                .stream()
                .filter(mappingInfo -> mappingInfo.support(request))
                .findFirst()
                .map(handlerMapping::get)
                .orElseThrow(NotFoundHandlerException::new);
    }
}
