package sirup.admin.clitool;

import java.io.Console;
import java.util.Map;

@CliSecureActionsClass
public class DefaultSecureActions {

    @CliSecureAction(command = "logout", description = "Logout the current user")
    public static void logout(Console console, Map<String,String> argMap) {
        SirupCli.logout();
        SirupCli.clearScreen();
    }

}
