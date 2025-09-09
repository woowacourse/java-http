package org.apache.coyote.http11.dispatcher;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMethod {

    private final Object controller;
    private final Method method;

    public HandlerMethod(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Object invoke(HttpRequest req) {
        try {
            Map<String, String> queryParams = req.getMappingLine().getParameters();
            Map<String, String> bodyParams = parseFormUrlEncoded(req);

            Parameter[] params = method.getParameters();
            Object[] args = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                Class<?> t = params[i].getType();
                if (t == HttpRequest.class) {
                    args[i] = req;
                } else if (Map.class.isAssignableFrom(t)) {
                    args[i] = queryParams;
                } else {
                    throw new IllegalArgumentException("unsupported param type: " + t.getName());
                }
            }

            if (queryParams.isEmpty() && bodyParams.isEmpty()) {
                return method.invoke(controller);
            }
            else if (queryParams.isEmpty()) {
                return method.invoke(controller, bodyParams);
            } else if (bodyParams.isEmpty()) {
                return method.invoke(controller, queryParams);
            } else {
                return method.invoke(controller, queryParams, bodyParams);
            }


        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Map<String, String> parseFormUrlEncoded(HttpRequest req) {
        byte[] bodyBytes;
        try {
            bodyBytes = req.getBody();
        } catch (Exception e) {
            return Map.of();
        }

        if (bodyBytes == null || bodyBytes.length == 0) return Map.of();

        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        Map<String, String> result = new HashMap<>();

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            int idx = pair.indexOf('=');
            String rawKey = idx < 0 ? pair : pair.substring(0, idx);
            String rawVal = idx < 0 ? "" : pair.substring(idx + 1);

            String key = urlDecodeUtf8(rawKey);
            String value = urlDecodeUtf8(rawVal);

            if (!key.isEmpty()) {
                result.put(key, value);
            }
        }
        return result;
    }

    private static String urlDecodeUtf8(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
