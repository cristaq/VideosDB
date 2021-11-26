package entertainment;

import database.UserDatabase;
import java.util.ArrayList;
import java.util.Map;

public final class Show extends Video {
    private int numberOfSeasons;
    private ArrayList<Season> seasons;

    public Show(final String title, final int year, final ArrayList<String> genres,
                final ArrayList<String> actors, final int numberOfSeasons,
                final ArrayList<Season> seasons) {
        super(title, year, genres, actors);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    @Override
    public int getDuration() {
        int duration = 0;
        for (Season s : seasons) {
            duration += s.getDuration();
        }
        return duration;
    }

    @Override
    public double rating(final UserDatabase udb) {
        double rating = 0;
        int counter = 0;
        double ratingTotal = 0;

        for (Map.Entry<String, User> entry : udb.getUsers().entrySet()) {
            rating = 0;
            for (String key : entry.getValue().getRated().keySet()) {
                if (key.contains(getTitle())) {
                    rating += entry.getValue().getRated().get(key);
                }
            }
            ratingTotal += rating / numberOfSeasons;
            if (rating != 0) {
                counter++;
            }
        }
        if (counter == 0) {
            return 0;
        }
        return ratingTotal / counter;
    }
}
