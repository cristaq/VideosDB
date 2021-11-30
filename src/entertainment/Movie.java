package entertainment;

import java.util.ArrayList;

public final class Movie extends Video {
    private int duration;
    private ArrayList<Double> ratings;

    public Movie(final String title, final int year, final ArrayList<String> genres,
                 final ArrayList<String> actors, final int duration) {
        super(title, year, genres, actors);
        this.duration = duration;
        ratings = new ArrayList<>();
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public double rating() {
        double sum = 0;
        if (ratings.size() == 0) {
            return 0;
        }
        for (Double rating : ratings) {
            sum += rating;
        }
        return sum / ratings.size();
    }

    @Override
    public void addRating(final Double rating, final int seasonNumber) {
        ratings.add(rating);
    }
}
