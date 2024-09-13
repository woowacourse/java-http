package thread.stage1;

import java.util.ArrayList;
import java.util.List;

public class UserServlet {

    private final List<User> users = new ArrayList<>();

    public void service(final User user) {
        join(user);
    }

    private synchronized void join(final User user) {
        System.out.println("스레드가 실행됐다." +  (Thread.currentThread().getName()));
        if (!users.contains(user)) {
            System.out.println("스레드가 실행됐다." +  (Thread.currentThread().getName()));
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
