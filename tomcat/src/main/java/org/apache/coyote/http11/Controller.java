package org.apache.coyote.http11;

public interface Controller {
    boolean canHandle(String url);
}
