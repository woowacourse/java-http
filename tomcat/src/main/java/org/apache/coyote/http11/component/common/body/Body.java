package org.apache.coyote.http11.component.common.body;

public interface Body {

    String deserialize();

    String getContent(String key);
}
