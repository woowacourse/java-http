package org.apache.coyote.http11.mvc.view;

import org.apache.coyote.http11.common.ResourceContentType;

public class StaticResourceView implements View {

    private final String viewName;
    private final String contentType;

    public StaticResourceView(final String viewName, final String contentType) {
        this.viewName = viewName;
        this.contentType = contentType;
    }

    public static StaticResourceView of(final String viewName) {
        return new StaticResourceView(viewName, ResourceContentType.from(viewName).getContentType());
    }

    @Override
    public String renderView() {
        return StaticResourceResolver.resolveResource(viewName);
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
