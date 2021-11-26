package action;

import database.UserDatabase;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;
import java.io.IOException;

public interface Command {
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
                message = udb.getUsers().get(action.getUsername()).rate(action);
                break;
            default:
                return;
        }
        result.add(result.size(), fileWriter.writeFile(action.getActionId(), "", message));
    }
}

