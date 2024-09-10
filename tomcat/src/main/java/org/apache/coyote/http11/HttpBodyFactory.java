package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpBodyFactory {

    private static final Map<ContentType, HttpBodyConstructor> classifier = new HashMap<>();

    static {
        classifier.put(ContentType.APPLICATION_X_WWW_FORM_URL_ENCODED, HttpXW3UrlEncodedBody::new);
    }

    public static HttpBody generateDefault(char[] body) {
        HttpBodyConstructor httpBodyConstructor = classifier.get(ContentType.APPLICATION_X_WWW_FORM_URL_ENCODED);

        return httpBodyConstructor.newInstance(body);
    }

    public static HttpBody generateHttpBody(ContentType contentType, char[] body) {
        HttpBodyConstructor httpBodyConstructor = classifier.getOrDefault(contentType, HttpXW3UrlEncodedBody::new);

        return httpBodyConstructor.newInstance(body);
    }

    interface HttpBodyConstructor {

        HttpBody newInstance(char[] input);
    }
}
