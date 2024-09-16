package thread.stage1;

import java.util.ArrayList;
import java.util.List;

public class UserServlet {

    private final List<User> users = new ArrayList<>();

    public synchronized void service(final User user) {
        join(user);
    }

    private void join(final User user) {
        if (!users.contains(user)) {
            try {
                Thread.sleep(1); // Expected context switching to another thread
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
