package sirup.admin.clitool;

import org.reflections.Reflections;

import java.io.Console;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class SirupCli {

    private final Map<String, Method> methodMap;
    //private final Map<Set<String>, String> printMap;
    private final List<CliObject> cliObjects;
    private final String pack;
    private final Console console;

    //Security
    private LoginHandler loginHandler;
    private PrintCallback welcomeMessage = () -> {
        System.out.println("Welcome");
    };
    private static boolean loggedIn = false;
    public static void logout() {
        SirupCli.loggedIn = false;
    }

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        }
        catch (IOException | InterruptedException ignored) {}
    }

    public SirupCli(final String pack) {
        console = System.console();
        methodMap = new HashMap<>();
        //printMap = new HashMap<>();
        cliObjects = new ArrayList<>();
        this.pack = pack;
    }

    public SirupCli addWelcomeMessage(PrintCallback printCallback) {
        this.welcomeMessage = printCallback;
        return this;
    }

    public SirupCli addLoginHandler(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
        return this;
    }

    public void start() {
        parseCliCommands();
        DefaultActions.setCliObjects(this.cliObjects);
        loggedIn = loginHandler == null;
        if (loginHandler != null) {
            runSecure();
        }
        else {
            runUnsecure();
        }
    }

    private void runSecure() {
        parseCliSecureCommands();
        loggedIn = loginHandler.login(this.console);
        while (true) {
            while (!loggedIn) {
                loggedIn = loginHandler.login(this.console);
                if (!loggedIn) {
                    clearScreen();
                    System.out.println("Please try again");
                }
            }
            clearScreen();
            welcomeMessage.print();
            while (loggedIn) {
                runLoop();
            }
        }
    }

    private void runUnsecure() {
        while (true) {
            runLoop();
        }
    }

    private void runLoop() {
        System.out.print("Enter command: ");
        String input = console.readLine();
        String[] inputArgs = input.split(" ");
        if (inputArgs.length == 0) {
            return;
        }
        String command = inputArgs[0];
        Method method = methodMap.get(command);
        if (method != null) {
            try {
                Map<String,String> argMap = buildArgMap(inputArgs);
                Object[] params = { console, argMap };
                method.invoke(null, params);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String,String> buildArgMap(String[] inputArgs) {
        Map<String, String> argMap = new HashMap<>();
        for (int i = 1; i < inputArgs.length; i++) {
            if (inputArgs[i].startsWith("-")) {
                if (i + 1 < inputArgs.length && !inputArgs[i + 1].startsWith("-")) {
                    argMap.put(inputArgs[i], inputArgs[i + 1]);
                    i++;
                    continue;
                }
                argMap.put(inputArgs[i], "");
            }
        }
        return argMap;
    }

    private void parseCliCommands() {
        Reflections reflections = new Reflections(pack);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CliActionsClass.class);
        classes.forEach(clazz -> {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(CliAction.class)) {
                    CliAction a = method.getAnnotation(CliAction.class);
                    Set<CliObject.CliArg> cliArgs = new HashSet<>();
                    methodMap.put(a.command(), method);
                    if (!a.alias().isEmpty()) {
                        methodMap.put(a.alias(), method);
                    }
                    if (method.isAnnotationPresent(CliArgs.class)) {
                        for (CliArgs.CliArg arg : method.getAnnotation(CliArgs.class).value()) {
                            cliArgs.add(new CliObject.CliArg(arg.flag(), arg.arg(), arg.description()));
                        }
                    }
                    cliObjects.add(new CliObject(a.command(), a.alias(), a.description(), cliArgs));
                }
            }
        });
    }

    private void parseCliSecureCommands() {
        Reflections reflections = new Reflections(pack);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CliSecureActionsClass.class);
        classes.forEach(clazz -> {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(CliSecureAction.class)) {
                    CliSecureAction a = method.getAnnotation(CliSecureAction.class);
                    Set<CliObject.CliArg> cliArgs = new HashSet<>();
                    methodMap.put(
                            method.getAnnotation(CliSecureAction.class).command(),method);
                    if (!a.alias().isEmpty()) {
                        methodMap.put(a.alias(),method);
                    }
                    if (method.isAnnotationPresent(CliArgs.class)) {
                        for (CliArgs.CliArg arg : method.getAnnotation(CliArgs.class).value()) {
                            cliArgs.add(new CliObject.CliArg(arg.flag(), arg.arg(), arg.description()));
                        }
                    }
                    cliObjects.add(new CliObject(a.command(), a.alias(), a.description(), cliArgs));
                }
            }
        });
    }

    public record CliObject(String command, String alias, String description, Set<CliArg> args) {
        public record CliArg(String flag, String arg, String description) {}
    }

    public interface LoginHandler {
        boolean login(Console console);
    }

    public interface PrintCallback {
        void print();
    }
}
