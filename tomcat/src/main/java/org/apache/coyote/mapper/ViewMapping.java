package org.apache.coyote.mapper;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.view.StaticResourceResolver;
import org.apache.coyote.view.View;
import org.apache.coyote.view.ViewResolver;

public class ViewMapping {

    private final List<ViewResolver> viewResolvers = new ArrayList<>();

    public ViewMapping() {
        viewResolvers.add(new StaticResourceResolver());
    }

    public ViewResolver resolveView(String viewPath) {
        for (ViewResolver viewResolver : viewResolvers) {
            if (viewResolver.support(viewPath)) {
                return viewResolver;
            }
        }
        return null;
    }
}
