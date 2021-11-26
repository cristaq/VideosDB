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


public final class VideoDatabase {
    private LinkedHashMap<String, Video> movies = new LinkedHashMap<>();
    private LinkedHashMap<String, Video> shows = new LinkedHashMap<>();


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

    public LinkedHashMap<String, Video> getMovies() {
        return movies;
    }

    public LinkedHashMap<String, Video> getShows() {
        return shows;
    }

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
        if (action.getCriteria().equals("longest")) {
            query.sort(new Comparator<Video>() {
                @Override
                public int compare(final Video o1, final Video o2) {
                    if(o1.getDuration() - o2.getDuration() == 0) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                    return o1.getDuration() - o2.getDuration();
                }
            });
        } else if (action.getCriteria().equals("favorite") || action.getCriteria().equals("favourite")) {
            query.sort(new Comparator<Video>() {
                @Override
                public int compare(final Video o1, final Video o2) {
                    if(o1.favourites(udb) - o2.favourites(udb) == 0) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                    return o1.favourites(udb) - o2.favourites(udb);
                }
            });
        } else if (action.getCriteria().equals("most_viewed")) {
            query.sort(new Comparator<Video>() {
                @Override
                public int compare(final Video o1, final Video o2) {
                    if(o1.views(udb) - o2.views(udb) == 0) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                    return o1.views(udb) - o2.views(udb);
                }
            });
        }else if (action.getCriteria().equals("ratings")) {
            query.sort(new Comparator<Video>() {
                @Override
                public int compare(final Video o1, final Video o2) {
                    if(Double.compare(o1.rating(udb), o2.rating(udb)) == 0) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
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

    public boolean filter(final ActionInputData action, final Video video,
                          final UserDatabase udb) {
        String year = String.valueOf(video.getYear());

        if (action.getFilters().get(0).get(0) != null) {
            if (!action.getFilters().get(0).contains(year)) {
                return false;
            }
        }

        if(action.getFilters().get(1).get(0) != null) {
            for (String s : action.getFilters().get(1)) {
                if (!video.getGenres().contains(s)) {
                    return false;
                }
            }
        }

        if (action.getCriteria().equals("favorite") || action.getCriteria().equals("favourite")) {
            if (video.favourites(udb) == 0) {
                return false;
            }
        } else if (action.getCriteria().equals("most_viewed") && video.views(udb) == 0) {
            return false;
        }else if (action.getCriteria().equals("ratings") && video.rating(udb) == 0) {
            return false;
        }

        return true;
    }
}
