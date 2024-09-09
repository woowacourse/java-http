package org.apache.coyote.http11.component.common.body;

public interface Body<T> {

    T serialize();

    String deserialize();
}
