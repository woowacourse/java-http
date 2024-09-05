package org.apache;

import org.apache.coyote.HttpResponse;

public class Serializer {

    public static byte[] serialize(HttpResponse response) {
        return response.getResponseMessage().getBytes();
    }
}
