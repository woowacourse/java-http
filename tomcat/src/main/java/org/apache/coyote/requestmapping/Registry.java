package org.apache.coyote.requestmapping;


import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.coyote.annotation.RequestMapping;
import org.apache.coyote.componentscan.RequestMappingScanner;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class Registry {

    private static Map<Mapping, Handler> requestMap = new HashMap<>();

    public static void register() {
        final Set<Method> requestMappingMethods = RequestMappingScanner.scan();
        for (Method method : requestMappingMethods) {
            final RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            final Mapping mappingKey = new Mapping(annotation.value(), annotation.httpMethod());
            requestMap.put(mappingKey, new MethodRequestHandler(method));
        }
    }

    private static boolean isStaticFileRequest(final String url) {
        try {
            final String filePath = Registry.class
                    .getClassLoader()
                    .getResource("static" + url)
                    .getPath();
            return new File(filePath).isFile();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static HttpResponse findMapping(HttpRequest httpRequest) {
        final String url = httpRequest.getUrl();
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        final Handler handler = requestMap.get(new Mapping(url, httpMethod));

        if (isMethodRequest(handler)) {
            return handler.handle(httpRequest);
        }

        if (isStaticFileRequest(url)) {
            return new FileRequestHandler(url).handle(httpRequest);
        }

        return HttpResponse.notFound().build();
    }

    private static boolean isMethodRequest(Handler handler) {
        return handler != null;
    }
}
