package action;

import database.MovieDatabase;
import database.UserDatabase;
import database.VideoDatabase;
import entertainment.Movie;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.FileWriter;
import java.io.IOException;

public interface Command {
    /*private int id;
    private String type;
    private String user;
    private String title;

    public Command(int id, String type, String user, String title) {
        this.id = id;
        this.type = type;
        this.user = user;
        this.title = title;
    }
    public void act(UserDatabase udb, JSONArray result, Writer fileWriter) throws IOException {
        String message;
        switch (type) {
            case "view" -> {
                message = udb.getUsers().get(user).view(title);
                result.add(result.size(), fileWriter.writeFile(id, "", message));
            }
            case "favorite" -> {
                message = udb.getUsers().get(user).favourite(title);
                result.add(result.size(), fileWriter.writeFile(id, "", message));
            }
        }
    }
}*/

    static void act(UserDatabase udb, JSONArray result, Writer fileWriter,
                    ActionInputData action) throws IOException {
        String message = "";
        switch (action.getType()) {
            case "view":
                message = udb.getUsers().get(action.getUsername()).view(action.getTitle());
                break;
            case "favorite":
            case "favourite":
                message = udb.getUsers().get(action.getUsername()).favourite(action.getTitle());
                break;
            case "rating":
                message = udb.getUsers().get(action.getUsername()).rate(action.getTitle(), action.getGrade());
                break;
            default:
                return;
        }
        result.add(result.size(), fileWriter.writeFile(action.getActionId(), "", message));
    }
}

