package com.techcourse.servlet.view;

public interface ViewResolver {

    View resolve(String viewName);

    boolean support(String viewName);
}
