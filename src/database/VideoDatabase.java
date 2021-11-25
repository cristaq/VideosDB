package database;

import entertainment.Movie;
import entertainment.Show;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;

import java.util.*;

public class VideoDatabase {
    private HashMap<String, Video> movies = new HashMap<>();
    private HashMap<String, Video> shows = new HashMap<>();


    public void addVideos(final List<MovieInputData> m, final List<SerialInputData> s) {
        for (MovieInputData i : m) {
            Movie newMovie = new Movie(
                    i.getTitle(),
                    i.getYear(),
                    i.getGenres(),
                    i.getCast(),
                    i.getDuration()
            );
            movies.put(i.getTitle(), newMovie);
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
            shows.put(i.getTitle(), newShow);
        }
    }

    public HashMap<String, Video> getMovies() {
        return movies;
    }

    public HashMap<String, Video> getShows() {
        return shows;
    }

    public String videoQ(UserDatabase udb, ActionInputData action) {
        StringBuilder message = new StringBuilder();
        int max = action.getNumber();
        List<Video> query = new ArrayList<>();

        if (action.getObjectType().equals("movies")) {
            for (Map.Entry<String, Video> entry : movies.entrySet()) {
                if(filter(action, entry.getValue(), udb)) {
                    query.add(entry.getValue());
                }
            }
        }
        else {
            for (Map.Entry<String, Video> entry : shows.entrySet()) {
                if(filter(action, entry.getValue(), udb)) {
                    query.add(entry.getValue());
                }
            }
        }

        if (action.getCriteria().equals("longest")) {
            query.sort(new Comparator<Video>() {
                @Override
                public int compare(Video o1, Video o2) {
                    return o1.getDuration() - o2.getDuration();
                }
            });
        }
        if (action.getCriteria().equals("favorite") || action.getCriteria().equals("favourite")) {
            query.sort(new Comparator<Video>() {
                @Override
                public int compare(Video o1, Video o2) {
                    return o1.favourites(udb) - o2.favourites(udb);
                }
            });
        }
        if (action.getCriteria().equals("most_viewed")) {
            query.sort(new Comparator<Video>() {
                @Override
                public int compare(Video o1, Video o2) {
                    return o1.views(udb) - o2.views(udb);
                }
            });
        }

        if (action.getCriteria().equals("ratings")) {
            query.sort(new Comparator<Video>() {
                @Override
                public int compare(Video o1, Video o2) {
                    return Double.compare(o1.rating(udb), o2.rating(udb));
                }
            });
        }

        message = new StringBuilder();
        message.append("Query result: [");
        int check = 0;
        if (action.getSortType().equals("asc")) {
            for (int i = 0; i < max && i < query.size(); i++) {
                message.append(query.get(i).getTitle());
                message.append(", ");
                check = 1;
            }
        }
        else {
            int counter = 0;
            for (int i = query.size() - 1; counter < max && i >= 0; i--) {
                message.append(query.get(i).getTitle());
                message.append(", ");
                check = 1;
                counter++;
            }
        }
        if (check == 1) {
            message.delete(message.length() - 2, message.length());
        }
        message.append("]");
        return message.toString();

    }

    public boolean filter(ActionInputData action, Video video, UserDatabase udb) {
        String year = String.valueOf(video.getYear());
        if (!action.getFilters().get(0).contains(year)) {
            return false;
        }

        for (String s : action.getFilters().get(1)) {
            if (!video.getGenres().contains(s)) {
                return false;
            }
        }

        if((action.getCriteria().equals("favorite") || action.getCriteria().equals("favourite"))
                && video.favourites(udb) == 0) {
            return false;
        }

        if(action.getCriteria().equals("most_viewed") && video.views(udb) == 0) {
            return false;
        }

        if(action.getCriteria().equals("ratings") && video.rating(udb) == 0) {
            return false;
        }

        return true;
    }
}
