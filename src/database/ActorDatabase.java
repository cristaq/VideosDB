package database;

import actor.Actor;
import fileio.ActorInputData;

import java.util.HashMap;
import java.util.List;

public class ActorDatabase {
    private HashMap<String, Actor> actors = new HashMap<>();

    public void addActors(List<ActorInputData> a) {
        for(ActorInputData i : a) {
            Actor newActor = new Actor(
                    i.getName(),
                    i.getCareerDescription(),
                    i.getFilmography(),
                    i.getAwards()
            );
            actors.put(i.getName(), newActor);
        }
    }

    public HashMap<String, Actor> getActors() {
        return actors;
    }
}
