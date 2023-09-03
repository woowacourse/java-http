package org.apache.coyote.http11.response.body;

import java.util.Objects;

public class ResponseBody {

    private final String content;

    private ResponseBody(final String content) {
        this.content = content;
    }

    public static ResponseBody from(final String content) {
        return new ResponseBody(content);
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ResponseBody that = (ResponseBody) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return "ResponseBody{" +
                "content='" + content + '\'' +
                '}';
    }
}
