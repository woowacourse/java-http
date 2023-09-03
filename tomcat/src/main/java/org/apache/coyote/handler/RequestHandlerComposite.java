package org.apache.coyote.handler;

import org.apache.coyote.exception.CoyoteHttpException;
import org.apache.coyote.handler.external.UserLoginRequestHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.request.HttpMethod.GET;

public class RequestHandlerComposite {

    private static final Map<MappingInfo, RequestHandler> mapping = new HashMap<>();

    static {
        mapping.put(new MappingInfo(GET.name(), "/"), new HtmlRequestHandler());
        mapping.put(new MappingInfo(GET.name(), "/index.html"), new HtmlRequestHandler());
        mapping.put(new MappingInfo(GET.name(), "/index"), new HtmlRequestHandler());
        mapping.put(new MappingInfo(GET.name(), "/login.html"), new HtmlRequestHandler());

        mapping.put(new MappingInfo(GET.name(), "/assets/chart-area.js"), new JsRequestHandler());
        mapping.put(new MappingInfo(GET.name(), "/assets/chart-pie.js"), new JsRequestHandler());
        mapping.put(new MappingInfo(GET.name(), "/assets/chart-bar.js"), new JsRequestHandler());
        mapping.put(new MappingInfo(GET.name(), "/js/scripts.js"), new JsRequestHandler());

        mapping.put(new MappingInfo(GET.name(), "/css/styles.css"), new CssRequestHandler());

        mapping.put(new MappingInfo(GET.name(), "/login"), new UserLoginRequestHandler());
    }

    public static HttpResponse handle(final HttpRequest httpRequest) {
        final RequestHandler requestHandler = mapping.getOrDefault(MappingInfo.from(httpRequest), null);
        if (Objects.isNull(requestHandler)) {
            throw new CoyoteHttpException("매핑되지 않은 요청을 처리할 수 없습니다.");
        }

        return requestHandler.handle(httpRequest);
    }
}
