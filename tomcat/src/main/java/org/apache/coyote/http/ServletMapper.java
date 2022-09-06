package org.apache.coyote.http;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServletMapper {

    private final Set<Servlet> servlets;

    private ServletMapper(final Set<Servlet> servlets) {
        this.servlets = servlets;
    }

    public static ServletMapper of(final Servlet... servlets) {
        final Set<Servlet> servletSet = new HashSet<>(List.of(servlets));

        return new ServletMapper(servletSet);
    }

    public Servlet find(final HttpRequest httpRequest) {
        return servlets.stream()
                .filter(it -> it.isMatch(httpRequest))
                .findFirst()
                .orElseGet(GeneralServlet::new);
    }
}
