package org.apache.catalina;

import nextstep.jwp.model.User;

import org.apache.coyote.http11.response.HttpResponse;

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
     * @param user Users who accessed the site
     * @param httpResponse HTTP response that has Session to be added
     */
    void add(final User user, final HttpResponse httpResponse);

    /**
     * Remove this Session from the active Sessions for this Manager.
     *
     * @param sessionId Session ID to be removed
     */
    void remove(final String sessionId);
}
