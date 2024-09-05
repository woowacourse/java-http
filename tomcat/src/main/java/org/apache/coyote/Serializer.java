package org.apache.coyote;

public class Serializer {

    public static byte[] serialize(HttpResponse response) {
        return response.getResponseMessage().getBytes();
    }
}
