package org.apache.catalina.webresources;

public interface WebResource {

    boolean exists();
    String getContent();
    String getContentType();
}
