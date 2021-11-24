package database;

import entertainment.Movie;
import entertainment.Season;
import entertainment.Show;
import fileio.ActionInputData;
import fileio.SerialInputData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ShowDatabase {
    private HashMap<String, Show> shows = new HashMap<>();
    public void addShows(final List<SerialInputData> s) {
        for(SerialInputData i : s) {
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

    public HashMap<String, Show> getShows() {
        return shows;
    }

    public String showQ(UserDatabase udb, ActionInputData action) {
        StringBuilder message = new StringBuilder();
        int max = action.getNumber();
        TreeMap<Integer, String> query = new TreeMap<>();
        for (Map.Entry<String, Show> entry : shows.entrySet()) {
            if (max == 0) {
                break;
            }

            if (!filter(action, entry.getValue())) {
                continue;
            }

            if(action.getCriteria().equals("longest")) {
                query.put(entry.getValue().getDuration(), entry.getKey());
                max--;
            }

            if(action.getCriteria().equals("favorite") || action.getCriteria().equals("favourite")) {
                query.put(entry.getValue().favourites(udb), entry.getKey());
                max--;
            }

            if(action.getCriteria().equals("most_viewed")) {
                String title = entry.getKey();
                Show show = entry.getValue();
                query.put(show.views(udb), title);
                max--;
            }
        }

        message = new StringBuilder();
        message.append("Query result: [");
        int check = 0;
        for(Map.Entry<Integer, String> sorted : query.entrySet()) {
            message.append(sorted.getValue());
            message.append(", ");
            check = 1;
        }
        if(check == 1) {
            message.delete(message.length() - 2, message.length());
        }
        message.append("]");
        String s = message.toString();
        return s;
    }

    public boolean filter(ActionInputData action, Show show) {
        String year = String.valueOf(show.getYear());
        if(!action.getFilters().get(0).contains(year)) {
            return false;
        }

        for(String s : action.getFilters().get(1)) {
            if(!show.getGenres().contains(s)) {
                return false;
            }
        }
        return true;
    }
}
