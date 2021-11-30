package entertainment;

import database.UserDatabase;

import java.util.ArrayList;
import java.util.Map;

/**
 * Abstract class extended by Movie and Show. Contains all aspects
 * that the two have in common. The differences are settled with abstract
 * methods, to be implemented in Movie or Show.
 */
public abstract class Video {
    private String title;
    private int year;
    private ArrayList<String> genres;
    private ArrayList<String> actors;
    private int favourites;
    private int views;

    public Video(final String title, final int year, final ArrayList<String> genres,
                 final ArrayList<String> actors) {
        this.title = title;
        this.year = year;
        this.genres = genres;
        this.actors = actors;
    }

    /**
     * Initialise the view and favourite count. Used during the creation
     * of the database.
     * @param udb a database of users
     */
    public void initStats(final UserDatabase udb) {
        views = 0;
        favourites = 0;
        for (Map.Entry<String, User> entry : udb.getUsers().entrySet()) {
            if (entry.getValue().getHistory().containsKey(title)) {
                views += entry.getValue().getHistory().get(title);
            }
            if (entry.getValue().getFavoriteMovies().contains(title)) {
                favourites++;
            }
        }
    }

    /**
     * Increments view count. Used in view function of user.
     */
    public void addView() {
        views++;
    }

    /**
     * Increments favourites count. Used in favourite function of user.
     */
    public void addFavourites() {
        favourites++;
    }

    /**
     * Calculates the rating of a video. Implemented in Movie and Show since Shows
     * have season-based ratings, while Movies do not.
     * @return the rating of a video
     */
    public abstract double rating();

    /**
     * Implemented in Movie and Show, since Shows have seasons with their
     * own duration, while Movies do not.
     * @return the duration of a video
     */
    public abstract int getDuration();

    /**
     * Adds a rating to a video. Implemented in Movie and Show since Shows
     * have season-based ratings, while Movies do not.
     * @param rating the rating to be added
     * @param seasonNumber the season to be rated. Unused for movies.
     */
    public abstract void addRating(Double rating, int seasonNumber);

    public final String getTitle() {
        return title;
    }

    public final int getYear() {
        return year;
    }

    public final ArrayList<String> getGenres() {
        return genres;
    }

    public final ArrayList<String> getActors() {
        return actors;
    }

    public final int getFavourites() {
        return favourites;
    }

    public final int getViews() {
        return views;
    }
}
