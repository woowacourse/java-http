package thread.stage1;

import java.util.LinkedList;

public class Users {

    private final LinkedList<User> users;

    private Users(final LinkedList<User> users) {
        this.users = users;
    }

    public static Users from(final LinkedList<User> users) {
        return new Users(users);
    }

    public boolean contains(User user) {
        return users.contains(user);
    }

    public void add(User user) {
        users.add(user);
    }

    public int size() {
        return users.size();
    }
}
