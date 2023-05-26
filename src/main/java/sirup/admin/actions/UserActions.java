package sirup.admin.actions;

import sirup.admin.Env;
import sirup.admin.clitool.CliActionsClass;
import sirup.admin.clitool.CliSecureAction;
import sirup.admin.clitool.CliSecureActionsClass;
import sirup.admin.security.Security;
import sirup.service.auth.rpc.client.SystemAccess;

import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@CliSecureActionsClass
public class UserActions {

    @CliSecureAction(command = "new_admin", alias = "na", description = "Add a new admin user to the system")
    public static void addAdmin(Console console, Map<String,String> argMap) {
        boolean valid = false;
        String username = "";
        String password1 = "";
        String password2 = "";
        while (!valid) {
            System.out.print("Enter username: ");
            username = console.readLine();
            System.out.print("Enter password: ");
            password1 = String.valueOf(console.readPassword());
            System.out.print("Confirm password: ");
            password2 = String.valueOf(console.readPassword());
            valid = password1.equals(password2);
            if (!valid) {
                System.out.println("Try again");
            }
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(Env.USER_ADDRESS + ":" + Env.USER_PORT + "/api/v1/user"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{userName:" + username +
                                    ", password: " + password1 +
                                    ", systemAccess:" + SystemAccess.ADMIN.id + "}" ))
                    .headers(
                            "Token", Security.token(),
                            "UserId", Security.userId())
                    .build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) {
                return;
            }
            System.out.println("Admin user [" + username +"] created");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
