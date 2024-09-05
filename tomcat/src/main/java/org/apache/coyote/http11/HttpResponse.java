package org.apache.coyote.http11;

record HttpResponse(byte[] responseBody, String contentType) {
}
