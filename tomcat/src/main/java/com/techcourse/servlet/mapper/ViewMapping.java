package com.techcourse.servlet.mapper;

import java.util.ArrayList;
import java.util.List;
import com.techcourse.servlet.view.StaticResourceResolver;
import com.techcourse.servlet.view.ViewResolver;

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
