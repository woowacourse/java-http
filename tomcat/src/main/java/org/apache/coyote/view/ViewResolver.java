package org.apache.coyote.view;

public interface ViewResolver {

    View resolve(String viewName);

    boolean support(String viewName);
}
