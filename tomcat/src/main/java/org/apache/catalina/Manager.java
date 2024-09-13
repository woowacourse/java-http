package org.apache.catalina;

import java.io.IOException;
import org.apache.catalina.session.Session;

/**
 * A <b>Manager</b> manages the pool of Sessions that are associated with a
 * particular Container.  Different Manager implementations may support
 * value-added features such as the persistent storage of session data,
 * as well as migrating sessions for distributable web applications.
 * <p>
 * In order for a <code>Manager</code> implementation to successfully operate
 * with a <code>Context</code> implementation that implements reloading, it
 * must obey the following constraints:
 * <ul>
 * <li>Must implement <code>Lifecycle</code> so that the Context can indicate
 *     that a restart is required.
 * <li>Must allow a call to <code>stop()</code> to be followed by a call to
 *     <code>start()</code> on the same <code>Manager</code> instance.
 * </ul>
 *
 * @author Craig R. McClanahan
 */
public interface Manager {

    /**
     * Add this Session to the set of active Sessions for this Manager.
     *
     * @param session Session to be added
     */
    void add(Session session);

    /**
     * Return the active Session, associated with this Manager, with the specified session id (if any); otherwise return
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

    Session createSession();

    void setIdGenerator(IdGenerator idGenerator);
}
