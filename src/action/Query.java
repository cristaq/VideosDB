package action;

import database.MovieDatabase;
import database.ShowDatabase;
import database.UserDatabase;
import database.VideoDatabase;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;

public interface Query {
    static void act(UserDatabase udb, VideoDatabase videodb, JSONArray result, Writer fileWriter,
                    ActionInputData action) throws IOException {
        String message = "";
        switch (action.getObjectType()) {
            /*case "movies":
                message = moviedb.movieQ(udb, action);
                break;
            case "shows":
                message = showdb.showQ(udb, action);
                break;

             */
            case "movies":
            case "shows" :
                message = videodb.videoQ(udb, action);
        }
        result.add(result.size(), fileWriter.writeFile(action.getActionId(), "", message));
    }
}
