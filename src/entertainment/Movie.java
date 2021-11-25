package entertainment;

import actor.Actor;
import database.UserDatabase;
import fileio.ActionInputData;

import java.util.ArrayList;
import java.util.Map;

public class Movie extends Video {
    private int duration;

    public Movie(String title, int year, ArrayList<String> genres, ArrayList<String> actors, int duration) {
        super(title, year, genres, actors);
        this.duration = duration;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public double rating(UserDatabase udb) {
        double rating = 0;
        int counter = 0;
        for(Map.Entry<String, User> entry : udb.getUsers().entrySet()) {
            if(entry.getValue().getRated().containsKey(getTitle())) {
                counter++;
                rating += entry.getValue().getRated().get(getTitle());
            }
        }
        if(counter == 0)
            return 0;
        return rating / counter;
    }
}
