package org.apache.coyote.request;

import java.util.List;

public class MockRequestReader implements Reader {

    private final List<String> lines;

    private final String requestBody;

    public MockRequestReader(final List<String> lines, final String requestBody) {
        this.lines = lines;
        this.requestBody = requestBody;
    }

    @Override
    public String getFirstLine() {
        return lines.get(0);
    }

    @Override
    public List<String> getHeader() {
        lines.remove(0);
        return lines;
    }

    @Override
    public String getBody(final int bodyLength) {
        return requestBody;
    }
}
