package org.apache.catalina.session;

import com.techcourse.exception.ErrorMessage;
import com.techcourse.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String id;

    private final Map<String, Object> session = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return session.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        session.put(name, value);
    }

    public void removeAttribute(final String name) {
        session.remove(name);
    }

    public User getUser() {
        User user = (User) getAttribute("user");
        if(user==null){
            throw new IllegalArgumentException(ErrorMessage.ACCOUNT_NOT_FOUND.toString());
        }
        return user;
    }

}
