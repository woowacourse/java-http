package thread.stage1;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserServlet {

    private final List<User> users = new CopyOnWriteArrayList<>();

    public synchronized void service(final User user) {
        join(user);
    }

    private void join(final User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public int size() {
        return users.size();
    }

    public List<User> getUsers() {
        return users;
    }
}
