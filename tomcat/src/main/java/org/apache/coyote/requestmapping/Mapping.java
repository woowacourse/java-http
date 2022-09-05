package org.apache.coyote.requestmapping;

import java.util.Objects;
import org.apache.coyote.http.HttpMethod;

public class Mapping {

    private String url;
    private HttpMethod httpStatus;

    public Mapping(String url, HttpMethod httpStatus) {
        this.url = url;
        this.httpStatus = httpStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mapping mapping = (Mapping) o;
        return Objects.equals(url, mapping.url) && httpStatus == mapping.httpStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, httpStatus);
    }
}
