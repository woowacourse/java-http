package org.apache.coyote.http11.serdes;

public interface Serializer<T> {

    String serialize(T object);
}
