package org.apache.catalina;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;

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
    void add(HttpSession session);

    /**
     * Return the active Session, associated with this Manager, with the
     * specified session id (if any); otherwise return <code>null</code>.
     *
     * @param id The session id for the session to be returned
     *
     * @exception IllegalStateException if a new session cannot be
     *  instantiated for any reason
     * @exception IOException if an input/output error occurs while
     *  processing this request
     *
     * @return the request session or {@code null} if a session with the
     *         requested ID could not be found
     */
    HttpSession findSession(String id) throws IOException;

    /**
     * Remove this Session from the active Sessions for this Manager.
     *
     * @param session Session to be removed
     */
    void remove(HttpSession session);

    /**
     * Construct and return a new session object, based on the default settings specified by this Manager's properties.
     * The session id specified will be used as the session id. If a new session cannot be created for any reason,
     * return <code>null</code>.
     *
     * @param sessionId The session id which should be used to create the new session; if <code>null</code>, the session
     *                      id will be assigned by this method, and available via the getId() method of the returned
     *                      session.
     *
     * @exception IllegalStateException if a new session cannot be instantiated for any reason
     *
     * @return An empty Session object with the given ID or a newly created session ID if none was specified
     */
    HttpSession createSession(String sessionId);
}
