package org.apache.coyote.servlet;

import java.util.Objects;

import org.apache.coyote.servlet.servlets.Servlet;

public class Mapping {

    private final Servlet servlet;
    private final String url;

    public Mapping(final Servlet servlet, final String url) {
        this.servlet = servlet;
        this.url = url;
    }

    public boolean isMapping(final String url) {
        return this.url.equals(url);
    }

    public Servlet getServlet() {
        return servlet;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Mapping mapping = (Mapping) o;
        return Objects.equals(servlet, mapping.servlet) && Objects.equals(url, mapping.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servlet, url);
    }

    @Override
    public String toString() {
        return "Mapping{" +
            "servlet=" + servlet +
            ", url='" + url + '\'' +
            '}';
    }
}
