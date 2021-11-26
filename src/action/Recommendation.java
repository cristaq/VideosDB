package action;

import database.UserDatabase;
import database.VideoDatabase;
import entertainment.User;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.*;

public interface Recommendation {
    static void act(UserDatabase udb, VideoDatabase videodb, JSONArray result, Writer fileWriter,
                    ActionInputData action) throws IOException {
        String message = "";
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

    static String standard(final UserDatabase udb, final VideoDatabase videodb,
                            final ActionInputData action) {
        String message = "";
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

    static String bestUnseen(final UserDatabase udb, final VideoDatabase videodb,
                             final ActionInputData action) {
        String message = "";
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
            public int compare(Video o1, Video o2) {
                return Double.compare(o2.rating(udb), o1.rating(udb));
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

    static String popular(final UserDatabase udb, final VideoDatabase videodb,
                            final ActionInputData action) {
        List<Video> query = new ArrayList<>();
        User user = udb.getUsers().get(action.getUsername());

        if (user.getSubscriptionType().equals("BASIC")) {
            return "PopularRecommendation cannot be applied!";
        }

        for (Map.Entry<String, Video> entry : videodb.getMovies().entrySet()) {
            if (entry.getValue().views(udb) != 0) {
                query.add(entry.getValue());
            }
        }

        for (Map.Entry<String, Video> entry : videodb.getShows().entrySet()) {
            if (entry.getValue().views(udb) != 0) {
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
                    popularGenres.replace(s, oldValue + video.views(udb));
                }
            }
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(popularGenres.entrySet());
        entries.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
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

    static String favourite(final UserDatabase udb, final VideoDatabase videodb,
                         final ActionInputData action) {
        String message = "";
        List<Video> query = new ArrayList<>();
        User user = udb.getUsers().get(action.getUsername());

        if (user.getSubscriptionType().equals("BASIC")) {
            return "FavoriteRecommendation cannot be applied!";
        }

        for (Map.Entry<String, Video> entry : videodb.getMovies().entrySet()) {
            if (entry.getValue().favourites(udb) != 0) {
                query.add(entry.getValue());
            }
        }

        for (Map.Entry<String, Video> entry : videodb.getShows().entrySet()) {
            if (entry.getValue().favourites(udb) != 0) {
                query.add(entry.getValue());
            }
        }

        query.sort(new Comparator<Video>() {
            @Override
            public int compare(Video o1, Video o2) {
                return o2.favourites(udb) - o1.favourites(udb);
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
                if (Double.compare(o1.rating(udb), o2.rating(udb)) == 0) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
                return Double.compare(o1.rating(udb), o2.rating(udb));
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
