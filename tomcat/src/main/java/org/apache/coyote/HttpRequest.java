package org.apache.coyote;

import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.MimeType;

public interface HttpRequest {

    String getRequestURI();

    HttpMethod getMethod();

    String getPath();

    boolean isExistsSession();

    String getHeader(String header);

    MimeType getAcceptMimeType();

    String getCookie(String cookieName);

    Map<String, String> getParsedBody();

    Session getSession();
}
