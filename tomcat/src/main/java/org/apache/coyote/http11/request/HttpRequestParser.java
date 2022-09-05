package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.coyote.http11.request.model.HttpRequestStartLine;

public class HttpRequestParser {

    private static final int START_LINE = 0;
    private static final int METHOD = 0;
    private static final int URI = 1;
    private static final int VERSION = 2;

    private final List<String[]> values;

    public HttpRequestParser(final List<String[]> values) {
        this.values = values;
    }

    public HttpRequestStartLine getHttpRequestStartLine() {
        String[] split = values.get(START_LINE);
        return new HttpRequestStartLine(split[METHOD], split[URI], split[VERSION]);
    }

    public HttpHeaders getHeaders() {
        return HttpHeaders.of(collectHeaders());
    }

    private List<String[]> collectHeaders() {
        return IntStream.range(1, values.size())
                .takeWhile(this::isEndHeaders)
                .mapToObj(values::get)
                .collect(Collectors.toList());
    }

    private boolean isEndHeaders(final int i) {
        return values.get(i).length != 1 && !values.get(i)[0].equals("");
    }
}
