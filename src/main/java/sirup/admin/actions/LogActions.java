package sirup.admin.actions;

import sirup.admin.clitool.CliArgs;
import sirup.admin.clitool.CliSecureAction;
import sirup.admin.clitool.CliSecureActionsClass;
import sirup.admin.security.Security;
import sirup.service.log.rpc.client.LogClient;
import sirup.service.log.rpc.proto.LogDTO;

import java.io.Console;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CliSecureActionsClass
public class LogActions {

    private static final LogClient logger = LogClient.getInstance();

    @CliSecureAction(command = "logger", alias = "l", description = "Get a list of all the logged services")
    public static void getLogList(Console console, Map<String,String> argMap) {
        Optional<List<String>> optionalList = logger.getLogList(Security.userId(), Security.token());
        if (optionalList.isEmpty()) {
            System.out.println("no log list was found");
            return;
        }
        List<String> list = optionalList.get();
        list.forEach(System.out::println);
    }

    @CliSecureAction(command = "log_from", alias = "lf", description = "Get all the logs for a service")
    @CliArgs(value = {
            @CliArgs.CliArg(flag = "s", arg = "serviceName", description = "Specify the service to get logs from"),
            @CliArgs.CliArg(flag = "n", arg = "number", description = "Specify the amount of logs printed at a time")
    })
    public static void getLogsFrom(Console console, Map<String,String> argMap) {
        String serviceName = argMap.getOrDefault("-s","");
        if (serviceName.isEmpty()) {
            System.out.println("Available services:");
            Optional<List<String>> optionalList = logger.getLogList(Security.userId(), Security.token());
            if (optionalList.isEmpty()) {
                System.out.println("no log list was found");
                return;
            }
            List<String> list = optionalList.get();
            for(int i = 0; i < list.size(); i++) {
                System.out.println(i + " - " + list.get(i));
            }
            int index = -1;
            while(index == -1) {
                try {
                    System.out.print("Select service: ");
                    index = Integer.parseInt(console.readLine());
                    serviceName = list.get(index);
                } catch (NumberFormatException e) {
                    index = -1;
                }
            }
        }
        Optional<List<LogDTO>> optionalList = logger.getLogsFrom(serviceName, Security.userId(), Security.token());
        if (optionalList.isEmpty()) {
            System.out.println("no logs were found");
            return;
        }
        List<LogDTO> list = optionalList.get();
        System.out.println("Date                          : Level\t: Message");
        System.out.println("=================================================");
        boolean printMore = true;
        int increment = 10;
        String nString = argMap.get("-n");
        if (nString != null) {
            try {
                increment = Integer.parseInt(nString);
            }
            catch (NumberFormatException ignored) {}
        }
        int n = increment;
        int s = 0;
        while (printMore) {
            printMore = printNLogs(s, n, list);
            System.out.println("=== Press 'Enter' to view the next " + increment + ". Enter 'Q' to stop === viewing " + n + " / " + list.size() + " ===");
            String input = console.readLine();
            if (input.equals("q")) {
                return;
            }
            n = Math.min(n + increment, list.size());
            s += increment;
        }
    }
    private static boolean printNLogs(int s, int n, final List<LogDTO> logList) {
        if (s >= logList.size()) {
            return false;
        }
        for (int i = s; i < n; i++) {
            LogDTO _log = logList.get(i);
            System.out.println(_log.getDate() + " : " + _log.getLevel() + "\t: " + _log.getMessage());
        }
        return n < logList.size();
    }
}
