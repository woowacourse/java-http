package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class RequestBody {
    private final Map<String, String> body;

    public RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public RequestBody() {
        this.body = new HashMap<>();
    }

    public Map<String, String> body() {
        return new HashMap<>(body);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (RequestBody) obj;
        return Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
