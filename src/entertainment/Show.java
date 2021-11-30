package entertainment;

import java.util.ArrayList;

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
    public double rating() {
        double sum = 0;
        double totalSum = 0;
        for (Season season : seasons) {
            sum = 0;
            for (Double rating : season.getRatings()) {
                sum += rating;
            }
            if (season.getRatings().size() != 0) {
                totalSum += sum / season.getRatings().size();
            }
        }
        if (totalSum == 0) {
            return 0;
        }
        return totalSum / numberOfSeasons;
    }

    @Override
    public void addRating(final Double rating, final int seasonNumber) {
        seasons.get(seasonNumber - 1).getRatings().add(rating);
    }
}
