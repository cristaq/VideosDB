package actor;

import database.UserDatabase;
import database.VideoDatabase;
import fileio.ActionInputData;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Actor {
    private String name;
    private String careerDescription;
    private ArrayList<String> filmography;
    private Map<ActorsAwards, Integer> awards;

    public Actor(final String name, final String careerDescription,
                 final ArrayList<String> filmography,
                 final Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = awards;
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    /**
     * Calculates the average rating of an actor. That is, the average rating
     * of all videos in which they play
     * @return the rating of an actor
     */
    public double average(final UserDatabase udb, final VideoDatabase videodb) {
        double rating = 0;
        int count = 0;
        double videoRating = 0;
        for (String s : filmography) {
            if (videodb.getMovies().containsKey(s)) {
                videoRating = videodb.getMovies().get(s).rating();
                if (Double.compare(videoRating, 0) > 0) {
                    rating += videodb.getMovies().get(s).rating();
                    count++;
                }
            } else if (videodb.getShows().containsKey(s)) {
                videoRating = videodb.getShows().get(s).rating();
                if (Double.compare(videoRating, 0) > 0) {
                    rating += videodb.getShows().get(s).rating();
                    count++;
                }
            }
        }
        if (count == 0) {
            return 0;
        }
        return rating / count;
    }

    /**
     * @return searches the description for words
     * specified in action and returns true if it finds them
     */
    public boolean searchDescription(final ActionInputData action) {
        for (String s : action.getFilters().get(2)) {
            String check = "\\b(?i)" + s + "\\b";
            Pattern pattern = Pattern.compile(check);
            Matcher matcher = pattern.matcher(careerDescription);
            if (!matcher.find()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return calculates the number of awards an actor has and
     * returns it
     */
    public int numberOfAwards() {
        int awardsNumber = 0;
        for (ActorsAwards key : awards.keySet()) {
            awardsNumber += awards.get(key);
        }
        return awardsNumber;
    }

}
