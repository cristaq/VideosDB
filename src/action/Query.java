package action;

import database.*;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;

public interface Query {
    static void act(UserDatabase udb, VideoDatabase videodb, ActorDatabase actordb,
                    JSONArray result, Writer fileWriter,
                    ActionInputData action) throws IOException {
        String message = "";
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
