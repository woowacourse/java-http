package org.apache.catalina.resource;

import org.apache.coyote.http11.response.ContentType;

public record Resource(String content, ContentType contentType) {
}
