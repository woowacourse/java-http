package org.apache.coyote;

import org.apache.coyote.http11.controller.impl.HelloController;
import org.apache.coyote.http11.controller.impl.IndexController;
import org.apache.coyote.http11.controller.impl.LoginController;
import org.apache.coyote.http11.controller.impl.RegisterController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.controller.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class RequestRouter {

    private static final Map<String, Controller> mappingMethods;

    static {
        final Map<String, Controller> methods = new HashMap<>();
        methods.put("/", new HelloController());
        methods.put("/index.html", new IndexController());
        methods.put("/login", new LoginController());
        methods.put("/register", new RegisterController());

        mappingMethods = Collections.unmodifiableMap(methods);
    }

    public void route(final HttpRequest request, final HttpResponse response) throws Exception {
        if (mappingMethods.containsKey(request.getRequestURI())) {
            final Controller controller = mappingMethods.get(request.getRequestURI());
            controller.service(request, response);
            return;
        }

        response.setHttpStatus(HttpStatus.OK);
        final ViewResolver viewResolver = new ViewResolver(Path.of(request.getRequestURI()));
        response.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
        response.write(Files.readString(viewResolver.getResourcePath()));
    }
}
