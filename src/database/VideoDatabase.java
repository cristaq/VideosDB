package database;

import entertainment.Movie;
import entertainment.Show;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VideoDatabase {
    private HashMap<String, Video> videos = new HashMap<>();

    public void addVideos(final List<MovieInputData> m, final List<SerialInputData> s) {
        for (MovieInputData i : m) {
            Movie newMovie = new Movie(
                    i.getTitle(),
                    i.getYear(),
                    i.getGenres(),
                    i.getCast(),
                    i.getDuration()
            );
            videos.put(i.getTitle(), newMovie);
        }

        for (SerialInputData i : s) {
            Show newShow = new Show(
                    i.getTitle(),
                    i.getYear(),
                    i.getGenres(),
                    i.getCast(),
                    i.getNumberSeason(),
                    i.getSeasons()
            );
            videos.put(i.getTitle(), newShow);
        }
    }

    public HashMap<String, Video> getVideos() {
        return videos;
    }

    public String videoQ(UserDatabase udb, ActionInputData action) {
        StringBuilder message = new StringBuilder();
        int max = action.getNumber();
        TreeMap<Integer, String> query = new TreeMap<>();
        for (Map.Entry<String, Video> entry : videos.entrySet()) {
            if (max == 0) {
                break;
            }

            if (!filter(action, entry.getValue()))  {
                continue;
            }

            if (action.getCriteria().equals("longest")) {
                query.put(entry.getValue().getDuration(), entry.getKey());
                max--;
            }

            if (action.getCriteria().equals("favorite") || action.getCriteria().equals("favourite")) {
                int favourites = entry.getValue().favourites(udb);
                if (favourites > 0) {
                    query.put(favourites, entry.getKey());
                    max--;
                }
            }

            if (action.getCriteria().equals("most_viewed")) {
                String title = entry.getKey();
                Video video = entry.getValue();
                int views = video.views(udb);
                if (views > 0) {
                    query.put(views, title);
                    max--;
                }
            }
        }

        message = new StringBuilder();
        message.append("Query result: [");
        int check = 0;
        if (action.getSortType().equals("asc")) {
            for (Map.Entry<Integer, String> sorted : query.entrySet()) {
                message.append(sorted.getValue());
                message.append(", ");
                check = 1;
            }
        }
        else {
            for (Integer key : query.descendingKeySet()){
                message.append(query.get(key));
                message.append(", ");
                check = 1;
            }
        }
        if (check == 1) {
            message.delete(message.length() - 2, message.length());
        }
        message.append("]");
        return message.toString();

    }

    public boolean filter(ActionInputData action, Video video) {
        String year = String.valueOf(video.getYear());
        if (!action.getFilters().get(0).contains(year)) {
            return false;
        }

        for (String s : action.getFilters().get(1)) {
            if (!video.getGenres().contains(s)) {
                return false;
            }
        }
        return true;
    }
}
