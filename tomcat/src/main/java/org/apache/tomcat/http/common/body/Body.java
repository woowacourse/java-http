package org.apache.tomcat.http.common.body;

public interface Body {

    String deserialize();

    String getContent(String key);
}
