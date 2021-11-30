package database;

import entertainment.User;
import fileio.ActionInputData;
import fileio.UserInputData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;


public final class UserDatabase {
    private LinkedHashMap<String, User> users = new LinkedHashMap<>();

    /**
     * Adds users into the LinkedHashMap. Here all users will be stored.
     * The LinkedHashMap provides easy access and maintains the order from input
     * @param u List of users given as input
     */
    public void addUsers(final List<UserInputData> u) {
        for (UserInputData i : u) {
            User newUser = new User(
                i.getUsername(),
                i.getSubscriptionType(),
                i.getHistory(),
                i.getFavoriteMovies()
            );
            users.put(i.getUsername(), newUser);
        }
    }

    public LinkedHashMap<String, User> getUsers() {
        return users;
    }

    /**
     * This function will handle the query that applies to users.
     * It creates an array with references for all users and sorts them
     * depending on the number of the ratings they have given.
     * @param action the action we must process
     * @return the message to be written in the JSONArray
     */
    public String userQ(final ActionInputData action) {
        List<User> query = new ArrayList<>();
        int max = action.getNumber();

        for (Map.Entry<String, User> entry : users.entrySet()) {
            if (entry.getValue().getRated().size() != 0) {
                query.add(entry.getValue());
            }
        }

        if (action.getSortType().equals("asc")) {
            query.sort(new Comparator<User>() {
                @Override
                public int compare(final User o1, final User o2) {
                    if (o1.getRated().size() - o2.getRated().size() == 0) {
                        return o1.getUsername().compareTo(o2.getUsername());
                    }
                    return o1.getRated().size() - o2.getRated().size();
                }
            });
        } else {
            query.sort(new Comparator<User>() {
                @Override
                public int compare(final User o1, final User o2) {
                    if (o2.getRated().size() - o1.getRated().size() == 0) {
                        return o2.getUsername().compareTo(o1.getUsername());
                    }
                    return o2.getRated().size() - o1.getRated().size();
                }
            });
        }


        StringBuilder message = new StringBuilder();
        message.append("Query result: [");
        int check = 0;
        for (int i = 0; i < max && i < query.size(); i++) {
            message.append(query.get(i).getUsername());
            message.append(", ");
            check = 1;
        }

        if (check == 1) {
            message.delete(message.length() - 2, message.length());
        }
        message.append("]");
        return message.toString();
    }
}
