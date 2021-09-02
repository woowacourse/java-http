package nextstep.jwp.http;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class HttpSession {

    private final String id;
    private final Map<String, User> storage;

    public HttpSession(String id) {
        this(id, new ConcurrentHashMap<>());
    }

    private HttpSession(String id, Map<String, User> storage) {
        this.id = id;
        this.storage = storage;
    }

    public void saveUser(String id, User user) {
        storage.put(id, user);
    }

    public Optional<User> getSession(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void removeSession(String id) {
        storage.remove(id);
    }

    public String getId() {
        return id;
    }

    public Map<String, User> getStorage() {
        return storage;
    }
}
