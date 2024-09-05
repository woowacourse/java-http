package servlet.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.coyote.request.HttpMethod;
import org.apache.coyote.request.Request;
import servlet.resource.ResourceHandler;
import servlet.resource.WelcomePageHandler;

public class SimpleUrlHandlerMapping implements HandlerMapping {

    private static final HttpMethod ALLOWED_METHOD = HttpMethod.GET;

    private final Map<String, Handler> urlMap; // todo ResourceHandlerRegistration

    public SimpleUrlHandlerMapping() {
        Map<String, Handler> urlMap = new HashMap<>();
        urlMap.put("/", new WelcomePageHandler());
        urlMap.put("/**", new ResourceHandler());
        this.urlMap = urlMap;
    }

    @Override
    public Handler getHandler(Request request) {
        if (!ALLOWED_METHOD.equals(request.getHttpMethod())) {
            return null;
        }

        if ("/".equals(request.getPath())) {
            return urlMap.get("/");
        }

        for (String url : urlMap.keySet()) {
            if (Pattern.matches(url.replace("/**", "/.*"), request.getPath())) {
                return urlMap.get(url);
            }
        }

        return urlMap.get(request);
    }
}
