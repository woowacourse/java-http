package org.apache.catalina.controller.resource;

record Resource(
        byte[] body,
        String contentType
) {
}
