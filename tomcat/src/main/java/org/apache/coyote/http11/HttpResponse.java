package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.view.View;

public class HttpResponse {

    private static final String LINE_DELIMITER = "\r\n";
    private static final String DELIMITER = " ";
    private static final String HTTP_VERSION = "HTTP/1.1";

    private HttpStatus status;
    private HttpHeaders headers;
    private View view;

    public HttpResponse() {
    }

    public void addHeader(String key, String value) {
        headers.add(key, value);
    }

    public byte[] getBytes() {
        return getResponse().getBytes();
    }

    private String getResponse() {
        List<String> response = new ArrayList<>();
        response.add(getStartLine());
        response.addAll(headers.getHeaders());
        response.add("");
        if (view != null) {
            response.add(view.getContent());
        }
        return String.join(LINE_DELIMITER, response);
    }

    private String getStartLine() {
        return String.join(DELIMITER,
                HTTP_VERSION,
                String.valueOf(status.getCode()),
                status.name()) + DELIMITER;
    }

    public int getCode() {
        return status.getCode();
    }

    public View getView() {
        return view;
    }

    public Optional<String> findHeaderByKey(String key) {
        return headers.findByKey(key);
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
