package org.apache.catalina;

import java.io.IOException;

public interface Manager {

    /**
     * Add this Session to the set of active Sessions for this Manager.
     *
     * @param session Session to be added
     */
    void add(Session session);

    /**
     * Return the active Session, associated with this Manager, with the specified session id (if any); otherwise
     * return
     * <code>null</code>.
     *
     * @param id The session id for the session to be returned
     * @return the request session or {@code null} if a session with the requested ID could not be found
     * @throws IllegalStateException if a new session cannot be instantiated for any reason
     * @throws IOException           if an input/output error occurs while processing this request
     */
    Session findSession(String id) throws IOException;

    /**
     * Remove this Session from the active Sessions for this Manager.
     *
     * @param session Session to be removed
     */
    void remove(Session session);

}
