package org.apache.catalina;

import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.session.Session;

/**
 * A <b>Manager</b> manages the pool of Sessions that are associated with a particular Container.  Different Manager
 * implementations may support value-added features such as the persistent storage of session data, as well as migrating
 * sessions for distributable web applications.
 * <p>
 * In order for a <code>Manager</code> implementation to successfully operate with a <code>Context</code> implementation
 * that implements reloading, it must obey the following constraints:
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

    void add(Session session);

    Optional<Session> findSession(String id) throws IOException;

    void remove(Session session);
}
