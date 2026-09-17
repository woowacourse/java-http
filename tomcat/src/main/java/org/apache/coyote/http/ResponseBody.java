package org.apache.coyote.http;

public interface ResponseBody {

    String contentType();

    byte[] bytes();
}
