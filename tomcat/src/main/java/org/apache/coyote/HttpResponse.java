package org.apache.coyote;

import org.apache.catalina.Session;
import org.apache.coyote.http11.MimeType;

public interface HttpResponse {

    String getResponseMessage();

    void ok(MimeType mimeType, String body);

    void found(String path);

    void notFound(String body);


    void setSession(Session session);


}
