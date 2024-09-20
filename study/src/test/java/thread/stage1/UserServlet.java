package thread.stage1;

import java.util.ArrayList;
import java.util.List;

public class UserServlet {

    private final List<User> users = new ArrayList<>();

    public void service(final User user) {
        join(user);
    }

    private void join(final User user) {
        //users.contains 에 브레이크 포인트를 걸면?
        // 처음에는 true 였다가, 다른 스레드가 작업이 끝나면 false 로 바꿔서 1개로 통과가 된다.
        if (!users.contains(user)) {
            // 동시성 의도적 발생
//            Thread.sleep(100);
            users.add(user);
        }
        // 지금 호출하는 녀석을 알수있는 방법?
    }

    public int size() {
        return users.size();
    }

    public List<User> getUsers() {
        return users;
    }
}
