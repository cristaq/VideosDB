package action;

import database.UserDatabase;
import database.VideoDatabase;
import entertainment.User;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Comparator;

public interface Recommendation {

    /**
     * handles all recommendations by calling specific functions depending
     * on the type of the recommendation specified in action.
     * @param udb a database of users
     * @param videodb a database of videos
     * @param result a JSONArray in which we will write the output
     * @param fileWriter used for writing in JSONArray
     * @param action the action we must process
     * @throws IOException required by Writer
     */
    static void act(UserDatabase udb, VideoDatabase videodb, JSONArray result, Writer fileWriter,
                    ActionInputData action) throws IOException {
        String message;
        switch (action.getType()) {
            case "standard":
                message = standard(udb, videodb, action);
                break;
            case "best_unseen":
                message = bestUnseen(udb, videodb, action);
                break;
            case "search":
                message = search(udb, videodb, action);
                break;
            case "favorite":
            case "favourite":
                message = favourite(udb, videodb, action);
                break;
            case "popular":
                message = popular(udb, videodb, action);
                break;
            default:
                return;
        }
        result.add(result.size(), fileWriter.writeFile(action.getActionId(), "", message));
    }

    /**
     * Function that handles the standard recommendation.
     * It returns the first unseen video by a user specified in action.
     * @param udb a database of users
     * @param videodb a database of videos
     * @param action the action to be implemented
     * @return message with the recommended video or error
     */
    static String standard(final UserDatabase udb, final VideoDatabase videodb,
                            final ActionInputData action) {
        String message;
        User user = udb.getUsers().get(action.getUsername());
        boolean found = false;
        String title = "";
        for (Map.Entry<String, Video> entry : videodb.getMovies().entrySet()) {
            if (!user.getHistory().containsKey(entry.getKey())) {
                found = true;
                title = entry.getKey();
                break;
            }
        }
        if (!found) {
            for (Map.Entry<String, Video> entry : videodb.getShows().entrySet()) {
                if (!user.getHistory().containsKey(entry.getKey())) {
                    found = true;
                    title = entry.getKey();
                    break;
                }
            }
        }
        if (found) {
            message = "StandardRecommendation result: " + title;
        } else {
            message = "StandardRecommendation cannot be applied!";
        }
        return message;
    }

    /**
     * Function that handles the bestUnseen recommendation.
     * It returns the first unseen video for a user specified in action,
     * from a queue ordered by ratings.
     * @param udb a database of users
     * @param videodb a database of videos
     * @param action the action to be implemented
     * @return message with the recommended video or error
     */
    static String bestUnseen(final UserDatabase udb, final VideoDatabase videodb,
                             final ActionInputData action) {
        String message;
        List<Video> query = new ArrayList<>();
        User user = udb.getUsers().get(action.getUsername());

        for (Map.Entry<String, Video> entry : videodb.getMovies().entrySet()) {
            query.add(entry.getValue());
        }

        for (Map.Entry<String, Video> entry : videodb.getShows().entrySet()) {
            query.add(entry.getValue());
        }

        query.sort(new Comparator<Video>() {
            @Override
            public int compare(final Video o1, final Video o2) {
                return Double.compare(o2.rating(), o1.rating());
            }
        });

        for (Video video : query) {
            if (!user.getHistory().containsKey(video.getTitle())) {
                message = "BestRatedUnseenRecommendation result: " + video.getTitle();
                return message;
            }
        }
        message = "BestRatedUnseenRecommendation cannot be applied!";
        return message;
    }

    /**
     * Function that handles the popular recommendation.
     * It returns the first unseen video with a specific genre,
     * for a user specified in action, from a queue ordered by genres with most views.
     * @param udb a database of users
     * @param videodb a database of videos
     * @param action the action to be implemented
     * @return message with the recommended video or error
     */
    static String popular(final UserDatabase udb, final VideoDatabase videodb,
                            final ActionInputData action) {
        List<Video> query = new ArrayList<>();
        User user = udb.getUsers().get(action.getUsername());

        if (user.getSubscriptionType().equals("BASIC")) {
            return "PopularRecommendation cannot be applied!";
        }

        for (Map.Entry<String, Video> entry : videodb.getMovies().entrySet()) {
            if (entry.getValue().getViews() != 0) {
                query.add(entry.getValue());
            }
        }

        for (Map.Entry<String, Video> entry : videodb.getShows().entrySet()) {
            if (entry.getValue().getViews() != 0) {
                query.add(entry.getValue());
            }
        }

        HashMap<String, Integer> popularGenres = new HashMap<>();

        for (Video video : query) {
            for (String s : video.getGenres()) {
                if (!popularGenres.containsKey(s)) {
                    popularGenres.put(s, 0);
                } else {
                    int oldValue = popularGenres.get(s);
                    popularGenres.replace(s, oldValue + video.getViews());
                }
            }
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(popularGenres.entrySet());
        entries.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(final Map.Entry<String, Integer> o1,
                               final Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (Map.Entry<String, Integer> entry : entries) {
            for (Video video : query) {
                if (video.getGenres().contains(entry.getKey())) {
                    if (!user.getHistory().containsKey(video.getTitle())) {
                        return "PopularRecommendation result: " + video.getTitle();
                    }
                }
            }
        }
        return "PopularRecommendation cannot be applied!";

    }

    /**
     * Function that handles the favourite recommendation.
     * It returns the first unseen video for a user specified in action,
     * from a queue ordered by favourite count.
     * @param udb a database of users
     * @param videodb a database of videos
     * @param action the action to be implemented
     * @return message with the recommended video or error
     */
    static String favourite(final UserDatabase udb, final VideoDatabase videodb,
                         final ActionInputData action) {
        String message = "";
        List<Video> query = new ArrayList<>();
        User user = udb.getUsers().get(action.getUsername());

        if (user.getSubscriptionType().equals("BASIC")) {
            return "FavoriteRecommendation cannot be applied!";
        }

        for (Map.Entry<String, Video> entry : videodb.getMovies().entrySet()) {
            if (entry.getValue().getFavourites() != 0) {
                query.add(entry.getValue());
            }
        }

        for (Map.Entry<String, Video> entry : videodb.getShows().entrySet()) {
            if (entry.getValue().getFavourites() != 0) {
                query.add(entry.getValue());
            }
        }

        query.sort(new Comparator<Video>() {
            @Override
            public int compare(final Video o1, final Video o2) {
                return o2.getFavourites() - o1.getFavourites();
            }
        });

        for (Video video : query) {
            if (!user.getHistory().containsKey(video.getTitle())) {
                message = "FavoriteRecommendation result: " + video.getTitle();
                return message;
            }
        }
        message = "FavoriteRecommendation cannot be applied!";
        return message;

    }

    /**
     * Function that handles the search recommendation.
     * It returns the all unseen videos for a user specified in action,
     * from a queue ordered by ratings.
     * @param udb a database of users
     * @param videodb a database of videos
     * @param action the action to be implemented
     * @return message with the recommended video or error
     */
    static String search(final UserDatabase udb, final VideoDatabase videodb,
                         final ActionInputData action) {
        StringBuilder message = new StringBuilder();
        List<Video> query = new ArrayList<>();
        User user = udb.getUsers().get(action.getUsername());

        if (user.getSubscriptionType().equals("BASIC")) {
            return "SearchRecommendation cannot be applied!";
        }

        for (Map.Entry<String, Video> entry : videodb.getMovies().entrySet()) {
            if (entry.getValue().getGenres().contains(action.getGenre())) {
                query.add(entry.getValue());
            }
        }

        for (Map.Entry<String, Video> entry : videodb.getShows().entrySet()) {
            if (entry.getValue().getGenres().contains(action.getGenre())) {
                query.add(entry.getValue());
            }
        }

        query.sort(new Comparator<Video>() {
            @Override
            public int compare(final Video o1, final Video o2) {
                if (Double.compare(o1.rating(), o2.rating()) == 0) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
                return Double.compare(o1.rating(), o2.rating());
            }
        });

        message.append("SearchRecommendation result: [");
        boolean found = false;
        for (Video video : query) {
            if (!user.getHistory().containsKey(video.getTitle())) {
                message.append(video.getTitle());
                message.append(", ");
                found = true;
            }
        }

        if (!found) {
            return "SearchRecommendation cannot be applied!";
        } else {
            message.delete(message.length() - 2, message.length());
            message.append("]");
            return message.toString();
        }
    }
}
