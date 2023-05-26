package sirup.admin;

import sirup.admin.clitool.SirupCli;
import sirup.admin.security.Security;
import sirup.service.auth.rpc.client.AuthClient;
import sirup.service.diag.rpc.client.DiagnosticsClient;
import sirup.service.log.rpc.client.LogClient;

public class Main {
    public static void main(String[] args) {
        AuthClient.init(Env.AUTH_ADDRESS,Env.AUTH_PORT);
        LogClient.init(Env.LOG_ADDRESS, Env.LOG_PORT, "AdminFrontend");
        DiagnosticsClient.init(Env.DIAG_ADDRESS,Env.DIAG_PORT);

        new SirupCli("sirup.admin")
                .addLoginHandler(Security::loginHandler)
                .addWelcomeMessage(Security::printWelcomeMessage)
                .start();
    }
}
