package jakarta.http;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public interface HttpSessionWrapper {

    HttpSession getSession(boolean sessionCreateIfAbsent, String sessionId) throws IOException;
}
