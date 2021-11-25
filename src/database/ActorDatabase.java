package database;

import actor.Actor;
import actor.ActorsAwards;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.ActorInputData;

import java.util.*;

public class ActorDatabase {
    private LinkedHashMap<String, Actor> actors = new LinkedHashMap<>();

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

    public String actorQ(UserDatabase udb, VideoDatabase videodb, ActionInputData action) {
        StringBuilder message = new StringBuilder();
        int max = action.getNumber();
        List<Actor> query = new ArrayList<>();

        for (Map.Entry<String, Actor> entry : actors.entrySet()) {
            if (filter(entry.getValue(), udb, videodb, action))
                query.add(entry.getValue());
        }

        if (action.getCriteria().equals("average")) {
            query.sort(new Comparator<Actor>() {
                @Override
                public int compare(Actor o1, Actor o2) {
                    if(Double.compare(o1.average(udb, videodb), o2.average(udb, videodb)) == 0) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    return Double.compare(o1.average(udb, videodb), o2.average(udb, videodb));
                }
            });
        }

        if(action.getCriteria().equals("filter_description")) {
            query.sort(new Comparator<Actor>() {
                @Override
                public int compare(Actor o1, Actor o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }

        if(action.getCriteria().equals("awards")) {
            query.sort(new Comparator<Actor>() {
                @Override
                public int compare(Actor o1, Actor o2) {
                    if(o1.number_of_awards() - o2.number_of_awards() == 0) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    return o1.number_of_awards() - o2.number_of_awards();
                }
            });
        }

        message = new StringBuilder();
        message.append("Query result: [");
        int check = 0;
        if (action.getSortType().equals("asc")) {
            for (int i = 0; i < max && i < query.size(); i++) {
                message.append(query.get(i).getName());
                message.append(", ");
                check = 1;
            }
        }
        else {
            int counter = 0;
            for (int i = query.size() - 1; counter < max && i >= 0; i--) {
                message.append(query.get(i).getName());
                message.append(", ");
                check = 1;
                counter++;
            }
        }
        if (check == 1) {
            message.delete(message.length() - 2, message.length());
        }
        message.append("]");
        return message.toString();

    }

    public boolean filter(Actor actor, UserDatabase udb,
                          VideoDatabase videodb, ActionInputData action) {
        if (action.getCriteria().equals("average")) {
            if(actor.average(udb, videodb) == 0) {
                return false;
            }
        }

        if(action.getCriteria().equals("filter_description")) {
            if(!actor.search_description(action)) {
                return false;
            }
        }

        if(action.getCriteria().equals("awards")) {
            for(String s : action.getFilters().get(3)) {
                int found = 0;
                for(ActorsAwards key : actor.getAwards().keySet()) {
                    if(key.toString().equals(s)) {
                        found = 1;
                        break;
                    }
                }
                if(found == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
