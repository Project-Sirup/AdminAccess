package sirup.admin.actions;

import com.google.gson.Gson;

import sirup.admin.Env;
import sirup.admin.clitool.CliSecureAction;
import sirup.admin.clitool.CliSecureActionsClass;

import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@CliSecureActionsClass
public class NotificationActions {

    private static final Gson gson = new Gson();

    @CliSecureAction(command = "remove_message", alias = "rm", description = "Remove the current global message")
    public static void removeMessage(Console console, Map<String,String> argsMap) {
        System.out.println("Clearing the message...");
        try {
            callNotificationService("other", "clear");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Message removed");
    }

    @CliSecureAction(command = "set_message", alias = "sm", description = "Set a new global message")
    public static void setGlobalMessage(Console console, Map<String,String> argsMap) {
        System.out.print("Enter the new message: ");
        String message = console.readLine();
        try {
            callNotificationService("global", message);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Message set");
    }

    private record NotificationObject(String eventType, String message) {}
    private static void callNotificationService(String type, String message) throws URISyntaxException, IOException, InterruptedException {
        //System.out.println("Notifying " + invite.receiverId());
        NotificationObject no = new NotificationObject(type, message);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(Env.NOTI_ADDRESS + ":" + Env.NOTI_PORT + "/api/v1/trigger"))
                .setHeader("Content-Type","Application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(no)))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
