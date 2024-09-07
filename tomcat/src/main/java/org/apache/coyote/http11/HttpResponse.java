package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.view.View;

public class HttpResponse {

    private HttpStatus status;
    private HttpHeaders headers;
    private View view;

    public HttpResponse() {
    }

    public HttpResponse(HttpStatus status, HttpHeaders headers, View view) {
        this.status = status;
        this.headers = headers;
        this.view = view;
    }

    public boolean isError() {
        return status != null && status.isClientError();
    }

    public byte[] getBytes() {
        return getResponse().getBytes();
    }

    private String getResponse() {
        List<String> response = new ArrayList<>();
        response.add("HTTP/1.1 " + status.getCode() + " " + status.name() + " ");
        response.addAll(headers.getHeaders());
        response.add("");
        response.add(view.getContent());
        return String.join("\r\n", response);
    }

    public int getCode() {
        return status.getCode();
    }

    public View getView() {
        return view;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public void setView(View view) {
        this.view = view;
    }
}
