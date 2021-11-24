package entertainment;

import actor.Actor;

import java.util.ArrayList;

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
}
