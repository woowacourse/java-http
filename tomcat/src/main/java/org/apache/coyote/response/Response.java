package org.apache.coyote.response;

public interface Response {

    String getPath();

    String getFileType();

    String getBodyString();

    int getStatusCode();

    String getStatusValue();
}
