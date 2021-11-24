package entertainment;

import database.UserDatabase;

import java.util.ArrayList;
import java.util.Map;

public class Video {
    private String title;
    private int year;
    private ArrayList<String> genres;
    private ArrayList<String> actors;
    private Double rating;

    public Video(String title, int year, ArrayList<String> genres, ArrayList<String> actors) {
        this.title = title;
        this.year = year;
        this.genres = genres;
        this.actors = actors;
    }

    public int views(UserDatabase udb) {
        int views = 0;
        for(Map.Entry<String, User> entry : udb.getUsers().entrySet()) {
            if(entry.getValue().getHistory().containsKey(title)) {
                views += entry.getValue().getHistory().get(title);
            }
        }
        return views;
    }

    public int favourites(UserDatabase udb) {
        int fav = 0;
        for(Map.Entry<String, User> entry : udb.getUsers().entrySet()) {
            if(entry.getValue().getFavoriteMovies().contains(title)) {
                fav++;
            }
        }
        return fav;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public Double getRating() {
        return rating;
    }

    //Overridden in movie and show
    public int getDuration() {
        return 0;
    }
}
