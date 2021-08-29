package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HttpHeaders {
    private final ConcurrentLinkedQueue<HttpHeader> headers;

    public HttpHeaders(HttpHeaders httpHeaders) {
        this(httpHeaders.headers);
    }

    public HttpHeaders() {
        this(new ConcurrentLinkedQueue<>());
    }

    public HttpHeaders(ConcurrentLinkedQueue<HttpHeader> headers) {
        this.headers = new ConcurrentLinkedQueue<>(headers);
    }

    public HttpHeaders addHeader(String name, String... values) {
        return addHeader(name, Arrays.asList(values));
    }

    public HttpHeaders addHeader(String name, List<String> values) {
        headers.add(new HttpHeader(name, values));
        return this;
    }

    public HttpHeaders addHeader(HttpHeader httpHeader) {
        headers.add(httpHeader);
        return this;
    }

    public HttpHeader poll() {
        return headers.poll();
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }
}
