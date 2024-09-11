package org.apache.coyote;

import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.http11.MimeType;

public interface HttpRequest {

    String getRequestURI();

    boolean isGet();

    boolean isPost();

    String getPath();

    boolean existsSession();

    String getHeader(String header);

    MimeType getAcceptMimeType() throws NoSuchFieldException;

    Map<String, String> getParsedBody();

    Session getSession();
}
