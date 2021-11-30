package action;

import database.UserDatabase;
import database.VideoDatabase;
import entertainment.User;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;
import java.io.IOException;

/**
 * Interface with only one static method. Used to handle the
 * command action type.
 */
public interface Command {
    /**
     * act is the only function in Command, and it handles
     * all actions of type command. The switch handles the type.
     * @param udb a database of users
     * @param result a JSONArray in which we will write the output
     * @param fileWriter used for writing in JSONArray
     * @param action the action we must process
     * @throws IOException required by Writer
     */
    static void act(UserDatabase udb, VideoDatabase videodb,
                    JSONArray result, Writer fileWriter,
                    ActionInputData action) throws IOException {
        String message;
        User user = udb.getUsers().get(action.getUsername());
        Video video;
        if (videodb.getMovies().containsKey(action.getTitle())) {
            video = videodb.getMovies().get(action.getTitle());
        } else {
            video = videodb.getShows().get(action.getTitle());
        }
        switch (action.getType()) {
            case "view":
                message = user.view(video, action.getTitle());
                break;
            case "favorite":
            case "favourite":
                message = user.favourite(video, action.getTitle());
                break;
            case "rating":
                message = user.rate(video, action);
                break;
            default:
                return;
        }
        result.add(result.size(), fileWriter.writeFile(action.getActionId(), "", message));
    }
}

