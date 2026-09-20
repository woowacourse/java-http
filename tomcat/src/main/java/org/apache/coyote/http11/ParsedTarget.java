package org.apache.coyote.http11;

public record ParsedTarget(
    String path,
    String queryString
) {

}
