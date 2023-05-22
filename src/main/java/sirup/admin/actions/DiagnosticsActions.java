package sirup.admin.actions;

import com.google.common.base.Strings;
import sirup.admin.clitool.CliAction;
import sirup.admin.clitool.CliActionsClass;
import sirup.admin.security.Security;
import sirup.service.diag.rpc.client.DiagnosticsClient;
import sirup.service.diag.rpc.proto.Report;
import sirup.service.diag.rpc.proto.Vitals;

import java.io.Console;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@CliActionsClass
public class DiagnosticsActions {

    private static DiagnosticsClient diag = DiagnosticsClient.getInstance();

    @CliAction(command = "diagnostics", alias = "d", description = "Run system diagnostics")
    public static void performDiagnostics(Console console, Map<String,String> argMap) {
        Optional<Report> optionalReport = diag.runDiagnostics(Security.token(), Security.userId());
        optionalReport.ifPresent(DiagnosticsActions::printReportTable);
    }

    private static void printReportTable(Report report) {
        String header = " Diagnostics Report ";
        int minHeaderLength = header.length() + 4;
        int maxNameLength = 0;
        for(Vitals vitals : report.getVitalsList()) {
            maxNameLength = Math.max(vitals.getServiceName().length(),maxNameLength);
        }
        minHeaderLength = Math.max(minHeaderLength, maxNameLength + 28);
        String format = "|%-" + minHeaderLength + "s|";
        String border = "|" + Strings.repeat("=",minHeaderLength) + "|";
        String spacer = "|" + Strings.repeat("-",minHeaderLength) + "|";
        System.out.println(border);
        System.out.printf((format) + "%n",header);
        System.out.println(border);
        String tableFormat = "| %-" + maxNameLength + "s | %-11s | %-9s |";
        System.out.printf((tableFormat) + "%n","Service","Responded","Time ms");
        System.out.println(spacer);
        AtomicLong totalTime = new AtomicLong();
        AtomicInteger totalResponded = new AtomicInteger();
        report.getVitalsList().forEach(vital -> {
            System.out.printf((tableFormat) + "%n",
                    vital.getServiceName(),
                    vital.getRunning(),
                    vital.getResponseTime() == -1 ? "-" : vital.getResponseTime() + "ms");
            if (vital.getRunning()) {
                totalTime.addAndGet(vital.getResponseTime());
                totalResponded.getAndIncrement();
            }
        });
        System.out.println(border);
        System.out.printf((format) + "%n"," Summary ");
        System.out.println(spacer);
        System.out.printf((format) + "%n"," Total Time: " + totalTime + "ms");
        System.out.printf((format) + "%n"," Responded: " + totalResponded + "/" + report.getVitalsList().size());
        System.out.println(border);
    }
}
