package org.apache.coyote.servlet;

import java.util.Objects;

import org.apache.coyote.servlet.servlets.AbstractServlet;

public class Mapping {

    private final AbstractServlet abstractServlet;
    private final String url;

    public Mapping(final AbstractServlet abstractServlet, final String url) {
        this.abstractServlet = abstractServlet;
        this.url = url;
    }

    public AbstractServlet getServlet() {
        return abstractServlet;
    }

    public boolean isSameUrl(final String url) {
        return this.url.equals(url);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Mapping mapping = (Mapping) o;
        return Objects.equals(abstractServlet, mapping.abstractServlet) && Objects.equals(url, mapping.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abstractServlet, url);
    }

    @Override
    public String toString() {
        return "Mapping{" +
            "servlet=" + abstractServlet +
            ", url='" + url + '\'' +
            '}';
    }
}
