package database;

import entertainment.User;
import fileio.UserInputData;
import java.util.HashMap;
import java.util.List;

public class UserDatabase {
    private HashMap<String, User> users = new HashMap<>();

    public void addUsers(final List<UserInputData> u) {
        for(UserInputData i : u) {
            User newUser = new User(
                i.getUsername(),
                i.getSubscriptionType(),
                i.getHistory(),
                i.getFavoriteMovies()
            );
            users.put(i.getUsername(), newUser);
        }
    }

    public HashMap<String, User> getUsers() {
        return users;
    }
}
