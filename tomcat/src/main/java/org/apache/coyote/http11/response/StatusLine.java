package org.apache.coyote.http11.response;

public record StatusLine(String protocolVersion, HttpStatusCode httpStatusCode) {
}
