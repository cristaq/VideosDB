package entertainment;

import database.UserDatabase;

import java.util.ArrayList;
import java.util.Map;

public class Video {
    private String title;
    private int year;
    private ArrayList<String> genres;
    private ArrayList<String> actors;

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

    //Overridden in movie and show
    public double rating(UserDatabase udb) {
        return 0;
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

    //Overridden in movie and show
    public int getDuration() {
        return 0;
    }

    public ArrayList<String> getActors() {
        return actors;
    }
}
