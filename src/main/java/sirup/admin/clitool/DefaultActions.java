package sirup.admin.clitool;

import java.io.Console;
import java.util.List;
import java.util.Map;

@CliActionsClass
public class DefaultActions {

    private static List<SirupCli.CliObject> cliObjects;

    public static void setCliObjects(List<SirupCli.CliObject> cliObjects) {
        DefaultActions.cliObjects = cliObjects;
    }

    @CliAction(command = "help", alias = "?", description = "Get a list of all the commands")
    public static void printHelp(Console console, Map<String,String> argMap) {
        System.out.println("Commands:");
        DefaultActions.cliObjects.forEach(cliObject -> {
            if (cliObject.alias() != null && !cliObject.alias().isEmpty()) {
                System.out.print(String.join(", ", cliObject.command(), cliObject.alias()));
            }
            else {
                System.out.print(cliObject.command());
            }
            if (cliObject.description() != null && !cliObject.description().isEmpty()) {
                System.out.println(" -> " + cliObject.description());
            }
            if (cliObject.args().size() > 0) {
                System.out.println("\tcommand options:");
                cliObject.args().forEach(cliArg -> {
                    System.out.print("\t-" + cliArg.flag());
                    if (!cliArg.arg().isEmpty()) {
                        System.out.print(" <" + cliArg.arg() + ">");
                    }
                    if (!cliArg.description().isEmpty()) {
                        System.out.println(" -> " + cliArg.description());
                    }
                });
            }
        });
    }

    @CliAction(command = "quit", alias = "q", description = "Closes the program")
    public static void quit(Console console, Map<String,String> argMap) {
        System.out.println("Bye");
        System.exit(0);
    }

    @CliAction(command = "clear", alias = "c", description = "Clears the console window")
    public static void clearScreen(Console console, Map<String,String> argMap) {
        SirupCli.clearScreen();
    }
}
