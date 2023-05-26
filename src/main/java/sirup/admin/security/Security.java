package sirup.admin.security;

import com.google.gson.Gson;
import sirup.admin.clitool.CliActionsClass;
import sirup.admin.clitool.CliSecureAction;
import sirup.admin.clitool.CliSecureActionsClass;
import sirup.service.auth.rpc.client.SystemAccess;
import sirup.admin.Env;

import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@CliSecureActionsClass
public class Security {

    private static final Gson gson = new Gson();

    private static String username;
    private static String token;
    public static String token() {
        return token;
    }
    private static String userId;
    public static String userId() {
        return userId;
    }

    @CliSecureAction(command = "who_am_i", alias = "me", description = "I dont know, how can you forget?")
    public static void whoAmI(Console console, Map<String,String> argMap) {
        System.out.println("Username: " + username);
        System.out.println("UserId: " + userId);
    }

    public static void printWelcomeMessage() {
        System.out.println("Welcome " + username);
    }

    public static boolean loginHandler(Console console) {
        System.out.print("username: ");
        String username = console.readLine();
        System.out.print("password: ");
        char[] pass = console.readPassword();
        return login(username, pass);
    }

    private static boolean login(String username, char[] password) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{userName:" + username +
                                    ", password:" + new String(password) + "}"))
                    .uri(new URI(Env.USER_ADDRESS +  ":" + Env.USER_PORT + "/api/v1/user/login"))
                    .build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            UserServiceResponse res = gson.fromJson(response.body(), UserServiceResponse.class);
            if (res.statusCode() != 200) {
                System.out.println(res.message());
                return false;
            }
            if (res.data().user().systemAccess().id != SystemAccess.ADMIN.id) {
                System.out.println("User must be an admin!");
                return false;
            }
            Security.token = res.data().token();
            Security.userId = res.data().user().userId();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        Security.username = username;
        return true;
    }

    public record UserServiceResponse(int statusCode, String message, UserServiceResponse.Data data) {
        public record User(String userId, String userName, String password, SystemAccess systemAccess) {}
        public record Data(String token, User user) {}
    }
}
