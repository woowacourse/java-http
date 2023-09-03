package org.apache.coyote.handler;

import org.apache.coyote.exception.CoyoteHttpException;
import org.apache.coyote.handler.get.UserLoginRequestGetHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.request.HttpMethod.GET;

public class RequestHandlerComposite {

    private static final Map<MappingInfo, RequestHandler> mapping = new HashMap<>();

    static {
        mapping.put(new MappingInfo(GET.name(), "/"), new HomeRequestHandler());
        mapping.put(new MappingInfo(GET.name(), "/login"), new UserLoginRequestGetHandler());
    }

    public static HttpResponse handle(final HttpRequest httpRequest) {
        final RequestHandler requestHandler = mapping.getOrDefault(MappingInfo.from(httpRequest), new ResourceRequestHandler());
        if (Objects.isNull(requestHandler)) {
            throw new CoyoteHttpException("매핑되지 않은 요청을 처리할 수 없습니다.");
        }

        return requestHandler.handle(httpRequest);
    }
}
