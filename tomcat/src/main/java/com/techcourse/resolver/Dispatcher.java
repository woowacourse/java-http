package com.techcourse.resolver;

import java.util.Map;
import java.util.TreeMap;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class Dispatcher {
    private static final Map<String, Resolver> resolvers = new TreeMap<>();

    public static HttpResponse dispatch(HttpRequest request) {
        if (resolvers.containsKey(request.getLocation())) {
            return resolvers.get(request.getLocation()).resolve(request);
        }

        StaticResourceResolver staticResourceResolver = new StaticResourceResolver();
        return staticResourceResolver.resolve(request);
    }

    public static void register(Resolver resolver) {
        resolvers.put(resolver.getLocation(), resolver);
    }
}
