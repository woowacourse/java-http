package org.apache.coyote.http11.mvc.view;

public class SimpleStringDataView implements View {

    private static final String CONTENT_TYPE = "text/plain;charset=utf-8";

    private final String data;

    public SimpleStringDataView(final String data) {
        this.data = data;
    }

    @Override
    public String renderView() {
        return data;
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }
}
