package com.techcourse.servlet.mapper;

import com.techcourse.servlet.view.StaticResourceResolver;
import com.techcourse.servlet.view.ViewResolver;
import java.util.ArrayList;
import java.util.List;

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
