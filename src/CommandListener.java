
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommandListener extends PluginListener {

    private final Map<String, MyBaseCommand> commands = new HashMap<String, MyBaseCommand>();
    private static final String PREFIX = "/finder";

    public CommandListener() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Command.class)) {
                for (String command : field.getAnnotation(Command.class).value()) {
                    try {
                        add(command.equals("") ? field.getName() : command, (MyBaseCommand) field.get(null));
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }
    }

    /**
     * Add a command to the player list.
     *
     * @param name
     * @param cmd
     */
    public void add(String name, MyBaseCommand cmd) {
        if (name != null && cmd != null) {
            if (!commands.containsValue(cmd)) {
                etc.getInstance().addCommand(PREFIX + " " + name, cmd.tooltip);
            }
            commands.put(name, cmd);
        }
    }

    @Override
    public boolean onCommand(Player player, String[] split) {
        if (!(split[0].equalsIgnoreCase(PREFIX) && player.canUseCommand(PREFIX))) {
            return false;
        }
        if (split.length < 2) {
            player.command("/help " + PREFIX);
            return true;
        }
        String[] args = new String[split.length - 2];
        System.arraycopy(split, 2, args, 0, args.length);
        commands.get(split[1].toLowerCase()).parseCommand(player, args);

        return true;
    }
    @Command
    public static final MyBaseCommand search = new MyBaseCommand("[query] - Search for plugins", "Usage: " + PREFIX + " search [query]", 1) {

        @Override
        public void execute(MessageReceiver caller, String[] parameters) {
            String[] results = Downloader.getSearchResults(etc.combineSplit(0, parameters, " "));
            caller.notify(Colors.Gold + "Results: " + Colors.White + etc.combineSplit(0, results, ", "));
        }
    };
    @Command
    public static final MyBaseCommand download = new MyBaseCommand("[plugin] - Downloads a plugin", "Usage: " + PREFIX + " download [plugin]", 1, 1) {

        @Override
        public void execute(final MessageReceiver caller, final String[] parameters) {
            new Thread("Downloader") {
                @Override
                public void run() {
                    String name = etc.combineSplit(0, parameters, " ");
                    caller.notify(Colors.Gray + "Starting download...");
                    if (Downloader.downloadPlugin(name, false))
                        caller.notify(Colors.LightGreen + "Finished downloading " + name + ".");
                    else {
                        caller.notify("Something went wrong while downloading " + name + ".");
                        caller.notify("You may want to check your console.");
                    }
                }
            }.start();
        }
    };
    @Command({"install", "update"})
    public static final MyBaseCommand install = new MyBaseCommand("[plugin] - Installs the given plugin", "Usage: " + PREFIX + " install [plugin]", 1, 1) {

        @Override
        public void execute(final MessageReceiver caller, final String[] parameters) {
            new Thread("Downloader") {
                @Override
                public void run() {
                    String name = etc.combineSplit(0, parameters, " ");
                    caller.notify(Colors.Gray + "Starting download...");
                    if (Downloader.downloadPlugin(name, true))
                        caller.notify(Colors.LightGreen + "Finished installing " + name + ".");
                    else {
                        caller.notify("Something went wrong while installing " + name + ".");
                        caller.notify("You may want to check your console.");
                    }
                }
            }.start();
        }
    };
    @Command
    public static final MyBaseCommand info = new MyBaseCommand("[plugin] - Shows info about the plugin", "Usage: " + PREFIX + " info [plugin]", 1, 1) {

        @Override
        public void execute(MessageReceiver caller, String[] parameters) {
            caller.notify(Colors.Gold
                    + String.format("[%s] %s version %s by %s (%s) (size: %s)",
                    (Object[]) Downloader.getPluginInfo(etc.combineSplit(0,
                    parameters, " "))));
        }
    };
}
