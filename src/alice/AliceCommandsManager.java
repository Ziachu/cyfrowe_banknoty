package alice;


import listeners.CommandListener;
import support.Commands;
import support.Series;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AliceCommandsManager {
    private boolean waiting_for_next_input;
    private String last_command;
    private CommandListener cmd_listener;

    public AliceCommandsManager(){
        waiting_for_next_input = false;
    }

    public void ManageUserInput(String user_input){

        if (!waiting_for_next_input) {
            switch (user_input){
                case "generate_id_series":

                    break;
                default:
                    cmd_listener.CommandEmitted("Wrong command", true);
                    break;
            }
        }
        else{

        }
    }
}
