package thread.stage1;

import java.util.ArrayList;
import java.util.List;

public class UserServlet {

    private final List<User> users = new ArrayList<>();

    public void service(final User user) throws InterruptedException {
        join(user);
    }

    private void join(final User user) throws InterruptedException {
        if (!users.contains(user)) {
            // 동시성 의도적 발생
            //Thread.sleep(100);
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
