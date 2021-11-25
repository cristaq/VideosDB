package actor;

import database.UserDatabase;
import database.VideoDatabase;
import entertainment.Movie;
import entertainment.Video;
import fileio.ActionInputData;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Actor {
    private String name;
    private String career_description;
    private ArrayList<String> filmography;
    private Map<ActorsAwards, Integer> awards;

    public Actor(String name, String career_description,
                 ArrayList<String> filmography, Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.career_description = career_description;
        this.filmography = filmography;
        this.awards = awards;
    }

    public String getName() {
        return name;
    }

    public String getCareer_description() {
        return career_description;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public double average(UserDatabase udb, VideoDatabase videodb) {
        double rating = 0;
        int count = 0;
        double videoRating = 0;
        for(String s : filmography) {
            if(videodb.getMovies().containsKey(s)) {
                videoRating = videodb.getMovies().get(s).rating(udb);
                if(Double.compare(videoRating, 0) > 0) {
                    rating += videodb.getMovies().get(s).rating(udb);
                    count++;
                }
            }
            else if(videodb.getShows().containsKey(s)) {
                videoRating = videodb.getShows().get(s).rating(udb);
                if(Double.compare(videoRating, 0) > 0) {
                    rating += videodb.getShows().get(s).rating(udb);
                    count++;
                }
            }
        }
        if(count == 0)
            return 0;
        return rating / count;
    }

    public boolean search_description(ActionInputData action) {
        for(String s : action.getFilters().get(2)) {
            String check = "\\b(?i)" + s + "\\b";
            Pattern pattern = Pattern.compile(check);
            Matcher matcher = pattern.matcher(career_description);
            if(!matcher.find())
                return false;
        }
        return true;
    }

    public int number_of_awards() {
        int awardsNumber = 0;
        for(ActorsAwards key : awards.keySet()) {
            awardsNumber += awards.get(key);
        }
        return awardsNumber;
    }

}
