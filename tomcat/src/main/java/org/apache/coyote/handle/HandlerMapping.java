package org.apache.coyote.handle;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.exception.http.NotFoundHandlerException;
import org.apache.coyote.handle.handler.FileHandler;
import org.apache.coyote.handle.handler.Handler;
import org.apache.coyote.handle.handler.HelloWorldHandler;
import org.apache.coyote.handle.handler.LoginHandler;
import org.apache.coyote.handle.handler.RegisterHandler;
import org.apache.coyote.handle.mapping.FileMappingInfo;
import org.apache.coyote.handle.mapping.HelloWorldMappingInfo;
import org.apache.coyote.handle.mapping.LoginMappingInfo;
import org.apache.coyote.handle.mapping.MappingInfo;
import org.apache.coyote.handle.mapping.RegisterMappingInfo;
import org.apache.coyote.request.HttpRequest;

public class HandlerMapping {

    private static final Map<MappingInfo, Handler> handlerMappings = new HashMap<>();

    static {
        handlerMappings.put(new HelloWorldMappingInfo(), new HelloWorldHandler());
        handlerMappings.put(new FileMappingInfo(), new FileHandler());
        handlerMappings.put(new LoginMappingInfo(), new LoginHandler());
        handlerMappings.put(new RegisterMappingInfo(), new RegisterHandler());
    }

    private HandlerMapping() {
    }

    public static HandlerMethod getHandlerMethod(final HttpRequest httpRequest) {
        return handlerMappings.keySet()
                .stream()
                .filter(mappingInfo -> mappingInfo.support(httpRequest))
                .findFirst()
                .map(mappingInfo ->
                        new HandlerMethod(
                                handlerMappings.get(mappingInfo),
                                mappingInfo.getMethod(httpRequest.getHttpMethod())
                        )
                )
                .orElseThrow(NotFoundHandlerException::new);
    }
}
