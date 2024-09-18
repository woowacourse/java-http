package com.techcourse.controller;

import java.util.Map;
import java.util.TreeMap;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class Dispatcher {
    private static final Map<String, HttpController> resolvers = new TreeMap<>();

    public static HttpResponse dispatch(HttpRequest request) throws Exception {
        HttpResponse response = new HttpResponse();

        if (resolvers.containsKey(request.getLocation())) {
            resolvers.get(request.getLocation()).service(request, response);
            return response;
        }

        String body = new ResourceFinder(request.getLocation(), request.getExtension()).getStaticResource(
                response);
        response.setBody(body);
        return response;
    }

    public static void register(HttpController resolver) {
        resolvers.put(resolver.getPath(), resolver);
    }
}
