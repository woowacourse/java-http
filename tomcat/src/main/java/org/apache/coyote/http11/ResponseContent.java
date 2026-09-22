package org.apache.coyote.http11;

record ResponseContent(String contentType, byte[] body) {

    private static final String PLAIN_TEXT_CONTENT_TYPE = "text/plain;charset=utf-8";

    static ResponseContent empty() {
        return new ResponseContent(PLAIN_TEXT_CONTENT_TYPE, new byte[0]);
    }
}
