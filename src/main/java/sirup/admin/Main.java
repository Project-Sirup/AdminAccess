package sirup.admin;

import sirup.admin.clitool.SirupCli;
import sirup.admin.security.Security;
import sirup.service.auth.rpc.client.AuthClient;
import sirup.service.diag.rpc.client.DiagnosticsClient;
import sirup.service.log.rpc.client.LogClient;

public class Main {
    public static void main(String[] args) {
        AuthClient.init("localhost",2101);
        LogClient.init("localhost", 2102, "AdminFrontend");
        DiagnosticsClient.init("localhost", 2105);

        new SirupCli("sirup.admin")
                .addLoginHandler(Security::loginHandler)
                .addWelcomeMessage(Security::printWelcomeMessage)
                .start();
    }
}
