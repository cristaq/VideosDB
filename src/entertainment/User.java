package entertainment;
import fileio.ActionInputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class User {
    private String username;
    private String subscriptionType;
    private Map<String, Integer> history;
    private ArrayList<String> favoriteMovies;
    private HashMap<String, Double> rated = new HashMap<>();

    public User(final String username, final String subscriptionType,
                 final Map<String, Integer> history, final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
    }

    /**
     * The action by which a user watches a video and adds it to its history table.
     * It also increments the view count in the specified video.
     * If it's already watched, increase the view count.
     * @param title the title of the video
     * @return message of the completed action
     */
    public String view(final Video video, final String title) {
        String message;
        if (history.containsKey(title)) {
            history.replace(title, history.get(title) + 1);
        } else {
            history.put(title, 1);
        }
        video.addView();
        message = "success -> " + title + " was viewed with total views of " + history.get(title);
        return message;
    }

    /**
     * The user adds a video to its favourites table.
     * It also increments the favourite count in the specified video.
     * If it is already marked as favourite, return error.
     * @param title the title of the video
     * @return message of the completed action or error
     */
    public String favourite(final Video video, final String title) {
        String message;
        if (!history.containsKey(title)) {
            message = "error -> " + title + " is not seen";
            return message;
        }
        if (favoriteMovies.contains(title)) {
            message = "error -> " + title + " is already in favourite list";
            return message;
        }

        favoriteMovies.add(title);
        video.addFavourites();
        message = "success -> " + title + " was added as favourite";
        return message;
    }

    /**
     * The user rates a video. Shows are rated by season. If it is already rated,
     * return error message.
     * It also adds the rating in the specified video.
     * @param video the video to be rated.
     * @param action The action to be implemented.
     * @return message of the completed action or error
     */
    public String rate(final Video video, final ActionInputData action) {
        String message = "";
        String title = "";

        //If a show is rated, we add it in the rated map with the season
        //number specified.
        if (action.getSeasonNumber() != 0) {
            title = action.getTitle() + " season " + action.getSeasonNumber();
        } else {
            title = action.getTitle();
        }
        if (rated.containsKey(title)) {
            message = "error -> " + action.getTitle() + " has been already rated";
            return message;
        }


        if (!history.containsKey(action.getTitle())) {
            message = "error -> " + action.getTitle() + " is not seen";
            return message;
        }

        rated.put(title, action.getGrade());
        video.addRating(action.getGrade(), action.getSeasonNumber());

        message = "success -> " + action.getTitle() + " was rated with " + action.getGrade()
                + " by " + username;
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

    public HashMap<String, Double> getRated() {
        return rated;
    }
}
