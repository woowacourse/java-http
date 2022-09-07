package org.apache.coyote.servlet;

import java.util.HashSet;
import java.util.Set;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.ResourceSearcher;
import org.apache.coyote.servlet.servlets.HelloWorldServlet;
import org.apache.coyote.servlet.servlets.LoginServlet;
import org.apache.coyote.servlet.servlets.RegisterServlet;
import org.apache.coyote.servlet.servlets.ResourceServlet;
import org.apache.coyote.servlet.servlets.Servlet;

public class ServletContainer {

    private static final ServletContainer SERVLET_CONTAINER = new ServletContainer();

    private static final Set<Mapping> MAPPINGS = new HashSet<>();

    private ServletContainer() {
    }

    public static ServletContainer init() {
        mapUrlToServlet(new HelloWorldServlet().init(), "/");
        mapUrlToServlet(new LoginServlet().init(), "/login");
        mapUrlToServlet(new RegisterServlet().init(), "/register");

        return SERVLET_CONTAINER;
    }

    public HttpResponse service(final HttpRequest httpRequest) {
        if (isResource(httpRequest)) {
            return new ResourceServlet().service(httpRequest);
        }

        final Servlet servlet = search(httpRequest);
        return servlet.service(httpRequest);
    }

    private static void mapUrlToServlet(final Servlet servlet, final String url) {
        final Mapping mapping = new Mapping(servlet, url);

        validateMappingIsNew(mapping);
        MAPPINGS.add(mapping);
    }

    private static void validateMappingIsNew(final Mapping mapping) {
        if (MAPPINGS.contains(mapping)) {
            throw new IllegalArgumentException(String.format("중복적으로 매핑되었습니다. [%s]", mapping));
        }
    }

    private boolean isResource(final HttpRequest httpRequest) {
        return ResourceSearcher.getInstance().isFile(httpRequest.getUrl());
    }

    private Servlet search(final HttpRequest httpRequest) {
        final String url = httpRequest.getUrl();

        return MAPPINGS.stream()
            .filter(mapping -> mapping.isMapping(url))
            .map(Mapping::getServlet)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("매핑되지 않은 url 입니다. [%s]", url)));
    }
}
