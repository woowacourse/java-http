package org.apache.coyote.http11;

public record StatusLine(String protocolVersion, HttpStatusCode httpStatusCode) {
}
