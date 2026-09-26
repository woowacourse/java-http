package org.apache.catalina;

import com.techcourse.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface SessionResolver {

    HttpSession getSession(Map<String, List<String>> headers, boolean create);

    boolean hasValidSession(Map<String, List<String>> headers);

    String getSessionId(HttpSession session);
}
