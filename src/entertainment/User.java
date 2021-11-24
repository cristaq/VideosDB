package entertainment;

import database.MovieDatabase;
import database.VideoDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String subscriptionType;
    private Map<String, Integer> history;
    private ArrayList<String> favoriteMovies;
    private HashMap<String, Double> rated = new HashMap<>();

    public User (String username, String subscriptionType, Map<String, Integer> history, ArrayList<String> favoriteMovies){
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
    }

    public String view(String title) {
        String message;
        if (history.containsKey(title)) {
            history.replace(title, history.get(title) + 1);
        }
        else {
            history.put(title, 1);
        }
        message = "success -> " + title + " was viewed with total views of " + history.get(title);
        return message;
    }

    public String favourite(String title) {
        String message;
        if (!history.containsKey(title)) {
            message = "error -> " + title + " is not seen";
            return message;
        }
        if (favoriteMovies.contains(title)) {
            message = "error -> " + title + " is already in favourite list";
            return message;
        }
        message = "success -> " + title + " was added as favourite";
        return message;
    }

    public String rate(String title, Double grade) {
        String message = "";
        if (rated.containsKey(title)) {
            message = "error -> " + title + " has been already rated";
            return message;
        }

        if (!history.containsKey(title)) {
            message = "error -> " + title + " is not seen";
            return message;
        }

        rated.put(title, grade);
        message = "success -> " + title + " was rated with " + grade + " by " + username;
        return message;

    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }
}