package sirup.admin.actions;

import sirup.admin.clitool.CliArgs;
import sirup.admin.clitool.CliSecureAction;
import sirup.admin.clitool.CliSecureActionsClass;
import sirup.admin.security.Security;
import sirup.service.auth.rpc.client.AuthClient;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.Console;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@CliSecureActionsClass
public class AuthActions {

    private static final AuthClient auth = AuthClient.getInstance();

    @CliSecureAction(command = "s_token", alias = "st", description = "Generates a new service token and serviceId")
    @CliArgs(value = {
            @CliArgs.CliArg(flag = "c", description = "Copy the output to the clipboard")
    })
    public static void getNewServiceToken(Console console, Map<String,String> argMap) {
        String serviceId = UUID.randomUUID().toString();
        String serviceToken = auth.serviceToken(Security.userId(), Security.token(), serviceId);
        System.out.println();
        System.out.println("SERVICE_ID="+serviceId);
        System.out.println("SERVICE_TOKEN="+serviceToken);
        System.out.println();
        boolean copy = argMap.get("-c") != null;
        if (copy) {
            String clip = "SERVICE_ID=" + serviceId + "\n\rSERVICE_TOKEN=" + serviceToken;
            if (System.getProperty("os.name").contains("Windows")) {
                Clipboard clipboard =  Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringData = new StringSelection(clip);
                clipboard.setContents(stringData, stringData);
                System.out.println("Output copied to clipboard");
            }
            else {
                System.out.println("Sorry, the -c flag does currently not work :/");
            }
        }
    }
}
