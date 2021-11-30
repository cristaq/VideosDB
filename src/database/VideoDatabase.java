package database;

import entertainment.Movie;
import entertainment.Show;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * Class that stores both movies and shows in two LinkedHashMaps
 */
public final class VideoDatabase {
    private LinkedHashMap<String, Video> movies = new LinkedHashMap<>();
    private LinkedHashMap<String, Video> shows = new LinkedHashMap<>();

    /**
     * Adds videos into the LinkedHashMaps. Movies and shows will be stored into
     * two separate maps.
     * The LinkedHashMap provides easy access and maintains the order from input
     * @param m List of movies given as input
     * @param s List of shows given as input
     */
    public void addVideos(final UserDatabase udb,
                          final List<MovieInputData> m, final List<SerialInputData> s) {
        for (MovieInputData i : m) {
            Movie newMovie = new Movie(
                    i.getTitle(),
                    i.getYear(),
                    i.getGenres(),
                    i.getCast(),
                    i.getDuration()
            );
            newMovie.initStats(udb);
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
            newShow.initStats(udb);
            shows.put(i.getTitle(), newShow);
        }
    }

    public LinkedHashMap<String, Video> getMovies() {
        return movies;
    }

    public LinkedHashMap<String, Video> getShows() {
        return shows;
    }

    /**
     * This function will handle all types of queries that apply to videos.
     * It creates an array with references for all videos and sorts them
     * depending on the type of query. It also generates the message with
     * the result of the query.
     * Query types: longest, most viewed, the best ratings
     * and most frequently added to favourites
     * @param udb a database of users
     * @param action the action we must process
     * @return the message to be written in the JSONArray
     */
    public String videoQ(final UserDatabase udb, final ActionInputData action) {
        StringBuilder message = new StringBuilder();
        int max = action.getNumber();
        List<Video> query = new ArrayList<>();

        if (action.getObjectType().equals("movies")) {
            for (Map.Entry<String, Video> entry : movies.entrySet()) {
                if (filter(action, entry.getValue(), udb)) {
                    query.add(entry.getValue());
                }
            }
        } else {
            for (Map.Entry<String, Video> entry : shows.entrySet()) {
                if (filter(action, entry.getValue(), udb)) {
                    query.add(entry.getValue());
                }
            }
        }
        switch (action.getCriteria()) {
            case "longest" -> query.sort(new Comparator<Video>() {
                @Override
                public int compare(final Video o1, final Video o2) {
                    if (o1.getDuration() - o2.getDuration() == 0) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                    return o1.getDuration() - o2.getDuration();
                }
            });
            case "favorite", "favourite" -> query.sort(new Comparator<Video>() {
                @Override
                public int compare(final Video o1, final Video o2) {
                    if (o1.getFavourites() - o2.getFavourites() == 0) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                    return o1.getFavourites() - o2.getFavourites();
                }
            });
            case "most_viewed" -> query.sort(new Comparator<Video>() {
                @Override
                public int compare(final Video o1, final Video o2) {
                    if (o1.getViews() - o2.getViews() == 0) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                    return o1.getViews() - o2.getViews();
                }
            });
            case "ratings" -> query.sort(new Comparator<Video>() {
                @Override
                public int compare(final Video o1, final Video o2) {
                    if (Double.compare(o1.rating(), o2.rating()) == 0) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                    return Double.compare(o1.rating(), o2.rating());
                }
            });
            default -> {
                return "error";
            }
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
        } else {
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

    /**
     * Used to apply filters on videos depending on action type.
     * Called in actorQ when deciding what videos to place in query.
     * @param action the action we must process
     * @param video the video to be filtered
     * @param udb a database of users
     * @return true if the video has all the necessary attributes to be
     * considered for the query
     */
    public boolean filter(final ActionInputData action, final Video video,
                          final UserDatabase udb) {
        String year = String.valueOf(video.getYear());

        if (action.getFilters().get(0).get(0) != null) {
            if (!action.getFilters().get(0).contains(year)) {
                return false;
            }
        }

        if (action.getFilters().get(1).get(0) != null) {
            for (String s : action.getFilters().get(1)) {
                if (!video.getGenres().contains(s)) {
                    return false;
                }
            }
        }

        if (action.getCriteria().equals("favorite") || action.getCriteria().equals("favourite")) {
            if (video.getFavourites() == 0) {
                return false;
            }
        } else if (action.getCriteria().equals("most_viewed") && video.getViews() == 0) {
            return false;
        } else if (action.getCriteria().equals("ratings") && video.rating() == 0) {
            return false;
        }

        return true;
    }
}
