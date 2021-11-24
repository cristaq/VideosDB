package actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


}
