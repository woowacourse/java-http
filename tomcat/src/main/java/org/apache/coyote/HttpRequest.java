package org.apache.coyote;

import java.util.Map;
import org.apache.catalina.Session;

public interface HttpRequest {

    String getRequestURI();

    String getMethod();

    String getPath();

    boolean isExistsSession();

    String getHeader(String header);

    String getCookie(String cookieName);

    Map<String, String> getParsedBody();

    Session getSession();
}
