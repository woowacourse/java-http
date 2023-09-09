package org.apache.coyote.http11.mvc.view;

public interface View {

    String renderView();

    String getContentType();
}
