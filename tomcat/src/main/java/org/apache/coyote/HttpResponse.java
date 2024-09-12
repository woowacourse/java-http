package org.apache.coyote;

import java.nio.charset.Charset;
import org.apache.catalina.Session;
import org.apache.coyote.http11.MimeType;

public interface HttpResponse {

    String getResponseMessage();

    void ok(MimeType mimeType, String body, Charset charSet);

    void found(String path);

    void notFound(String body, Charset charSet);

    void setSession(Session session);
}
