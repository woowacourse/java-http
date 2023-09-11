package org.apache.coyote.http11.view;

public interface View {

    String renderView();

    String getContentType();
}
