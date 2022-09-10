package org.apache.coyote.servlet;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.ResourceSearcher;
import org.apache.coyote.servlet.servlets.HelloWorldServlet;
import org.apache.coyote.servlet.servlets.LoginServlet;
import org.apache.coyote.servlet.servlets.RegisterServlet;
import org.apache.coyote.servlet.servlets.ResourceServlet;
import org.apache.coyote.servlet.servlets.AbstractServlet;
import org.apache.coyote.servlet.servlets.Servlet;

public class ServletContainer {

    private static final ServletContainer SERVLET_CONTAINER = new ServletContainer();

    private static final Set<Mapping> MAPPINGS = ConcurrentHashMap.newKeySet();

    private final SessionManager sessionManager;
    private final ResourceServlet resourceServlet;

    private ServletContainer() {
        sessionManager = SessionManager.init();
        resourceServlet = new ResourceServlet(sessionManager);
    }

    public static ServletContainer init() {
        final ServletContainer servletContainer = SERVLET_CONTAINER;

        mapUrlToServlet(new HelloWorldServlet(servletContainer.sessionManager), "/");
        mapUrlToServlet(new LoginServlet(servletContainer.sessionManager), "/login");
        mapUrlToServlet(new RegisterServlet(servletContainer.sessionManager), "/register");

        return servletContainer;
    }

    public Servlet getServlet(final HttpRequest httpRequest) {
        final String url = httpRequest.getUrl();
        if (isResource(url)) {
            return resourceServlet;
        }

        return MAPPINGS.stream()
            .filter(mapping -> mapping.isSameUrl(url))
            .map(Mapping::getServlet)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("매핑되지 않은 url 입니다. [%s]", url)));
    }

    private static void mapUrlToServlet(final AbstractServlet abstractServlet, final String url) {
        final Mapping mapping = new Mapping(abstractServlet, url);

        validateMappingIsNew(mapping);
        MAPPINGS.add(mapping);
    }

    private static void validateMappingIsNew(final Mapping mapping) {
        if (MAPPINGS.contains(mapping)) {
            throw new IllegalArgumentException(String.format("중복적으로 매핑되었습니다. [%s]", mapping));
        }
    }

    private boolean isResource(final String url) {
        return ResourceSearcher.getInstance().isFile(url);
    }
}
