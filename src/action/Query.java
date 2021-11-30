package action;

import database.ActorDatabase;
import database.UserDatabase;
import database.VideoDatabase;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;

/**
 * Interface with only one static method. Used to handle the
 * query action type.
 */
public interface Query {
    /**
     * act is the only function in Query, and it handles
     * all actions of type query. The switch handles the type.
     * @param udb a database of users
     * @param videodb a database of videos
     * @param actordb a database of actors
     * @param result a JSONArray in which we will write the output
     * @param fileWriter used for writing in JSONArray
     * @param action the action we must process
     * @throws IOException required by Writer
     */
    static void act(UserDatabase udb, VideoDatabase videodb, ActorDatabase actordb,
                    JSONArray result, Writer fileWriter,
                    ActionInputData action) throws IOException {
        String message;
        switch (action.getObjectType()) {
            case "movies":
            case "shows" :
                message = videodb.videoQ(udb, action);
                break;
            case "users":
                message = udb.userQ(action);
                break;
            case "actors":
                message = actordb.actorQ(udb, videodb, action);
                break;
            default:
                return;
        }
        result.add(result.size(), fileWriter.writeFile(action.getActionId(), "", message));
    }
}
