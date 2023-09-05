package org.apache.coyote.handler;

import org.apache.coyote.handler.get.UserLoginRequestGetHandler;
import org.apache.coyote.handler.get.UserRegisterRequestGetHandler;
import org.apache.coyote.handler.post.UserRegisterRequestPostHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.request.HttpMethod.GET;
import static org.apache.coyote.request.HttpMethod.POST;

public class RequestHandlerComposite {

    private RequestHandlerComposite() {
    }

    private static final Map<MappingInfo, RequestHandler> mapping = new HashMap<>();

    static {
        mapping.put(new MappingInfo(GET.name(), "/"), new HomeRequestHandler());
        mapping.put(new MappingInfo(GET.name(), "/login"), new UserLoginRequestGetHandler());
        mapping.put(new MappingInfo(GET.name(), "/register"), new UserRegisterRequestGetHandler());
        mapping.put(new MappingInfo(POST.name(), "/register"), new UserRegisterRequestPostHandler());
    }

    public static HttpResponse handle(final HttpRequest httpRequest) {
        final RequestHandler requestHandler = mapping.getOrDefault(MappingInfo.from(httpRequest), new ResourceRequestHandler());

        return requestHandler.handle(httpRequest);
    }
}
