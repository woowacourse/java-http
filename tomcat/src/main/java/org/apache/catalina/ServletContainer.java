package org.apache.catalina;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContainer {

    private static final Logger log = LoggerFactory.getLogger(ServletContainer.class);

    private final Map<String, Servlet> servlets = new LinkedHashMap<>();

    public void addServlet(final String urlPattern, final Servlet servlet) {
        servlets.put(urlPattern, servlet);
        servlet.init();
        log.info("Servlet registered: {} -> {}", urlPattern, servlet.getClass().getSimpleName());
    }

    public void service(final HttpRequest request, final HttpResponse response) {
        final String uri = request.getUri();
        final Servlet servlet = findServlet(uri);

        if (servlet != null) {
            servlet.service(request, response);
        } else {
            handleNotFound(response);
        }
    }

    private Servlet findServlet(final String uri) {
        if (servlets.containsKey(uri)) {
            return servlets.get(uri);
        }

        for (final Entry<String, Servlet> entry : servlets.entrySet()) {
            final String pattern = entry.getKey();
            if (pattern.endsWith("/*")) {
                final String prefix = pattern.substring(0, pattern.length() - 2);
                if (uri.startsWith(prefix)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    private void handleNotFound(final HttpResponse response) {
        response.setStatus(404);
        response.write("<html><body><h1>404 Not Found</h1></body></html>");
    }

    public void destroy() {
        for (final Servlet servlet : servlets.values()) {
            servlet.destroy();
        }
        servlets.clear();
    }
}
