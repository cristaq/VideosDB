package entertainment;

import actor.Actor;
import database.UserDatabase;

import java.util.ArrayList;
import java.util.Map;

public class Show extends Video{
    private int number_of_seasons;
    private ArrayList<Season> seasons;

    public Show(String title, int year, ArrayList<String> genres, ArrayList<String> actors,
                int number_of_seasons, ArrayList<Season> seasons) {
        super(title, year, genres, actors);
        this.number_of_seasons = number_of_seasons;
        this.seasons = seasons;
    }

    @Override
    public int getDuration() {
        int duration = 0;
        for(Season s : seasons) {
            duration += s.getDuration();
        }
        return duration;
    }

    @Override
    public double rating(UserDatabase udb) {
        double rating = 0;
        int counter = 0;
        double ratingTotal = 0;

        for(Map.Entry<String, User> entry : udb.getUsers().entrySet()) {
            rating = 0;
            for(String key : entry.getValue().getRated().keySet()) {
                if(key.contains(getTitle())) {
                    rating += entry.getValue().getRated().get(key);
                }
            }
            ratingTotal += rating / number_of_seasons;
            if(rating != 0) {
                counter++;
            }
        }
        if(counter == 0)
            return 0;
        return ratingTotal / counter;
    }
}
