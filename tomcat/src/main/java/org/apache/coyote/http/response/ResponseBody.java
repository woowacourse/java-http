package org.apache.coyote.http.response;

public interface ResponseBody {

    String contentType();

    byte[] bytes();
}
