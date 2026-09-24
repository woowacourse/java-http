package thread.stage1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserServlet {

    private final CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();

    public void service(final User user) {
        join(user);
    }

    private void join(final User user) {
        users.addIfAbsent(user);
    }

    public int size() {
        return users.size();
    }

    public List<User> getUsers() {
        return users;
    }
}
