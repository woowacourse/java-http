package thread.stage1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class UserServlet {

    private final List<User> users = new ArrayList<>();

    private final ReentrantLock lock = new ReentrantLock();

    public void service(final User user) {
        join(user);
    }

    private void join(final User user) {
        lock.lock();
        if (!users.contains(user)) {
            users.add(user);
        }
        lock.unlock();
    }

    public int size() {
        return users.size();
    }

    public List<User> getUsers() {
        return users;
    }
}
