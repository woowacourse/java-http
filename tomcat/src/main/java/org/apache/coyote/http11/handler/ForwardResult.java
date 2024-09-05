package org.apache.coyote.http11.handler;

record ForwardResult(boolean isRedirect, String path) {
}
