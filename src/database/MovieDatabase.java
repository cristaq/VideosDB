package database;

import entertainment.Movie;
import entertainment.User;
import fileio.ActionInputData;
import fileio.MovieInputData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MovieDatabase {
    private HashMap<String, Movie> movies = new HashMap<>();
    public void addMovies(final List<MovieInputData> m) {
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
    }

    public HashMap<String, Movie> getMovies() {
        return movies;
    }

    public String movieQ(UserDatabase udb, ActionInputData action) {
        StringBuilder message = new StringBuilder();
        int max = action.getNumber();
        TreeMap<Integer, String> query = new TreeMap<>();
        for (Map.Entry<String, Movie> entry : movies.entrySet()) {
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
                Movie movie = entry.getValue();
                int views = movie.views(udb);
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
        String s = message.toString();
        return s;

    }

    public boolean filter(ActionInputData action, Movie movie) {
        String year = String.valueOf(movie.getYear());
        if (!action.getFilters().get(0).contains(year)) {
            return false;
        }

        for (String s : action.getFilters().get(1)) {
            if (!movie.getGenres().contains(s)) {
                return false;
            }
        }
        return true;
    }
}
