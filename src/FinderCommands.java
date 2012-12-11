public class FinderCommands extends CommandHandler {

    public FinderCommands() {
        super(EggFinder.PREFIX);
        this.addAll(FinderCommands.class);
    }

    public boolean parseCommand(MessageReceiver caller, String[] split) {
        String[] args = new String[split.length - 2];
        System.arraycopy(split, 2, args, 0, args.length);
        BaseCommand bc = this.getCommand(split[1].toLowerCase());
        if (bc != null) {
            bc.parseCommand(caller, args);
            return true;
        }

        return false;
    }

    @Command
    public static final BaseCommand search = new BaseCommand(
            "[query] - Search for plugins",
            "Usage: /" + EggFinder.PREFIX + " search [query]", 1) {

        @Override
        public void execute(MessageReceiver caller, String[] parameters) {
            String[] results = Downloader.getSearchResults(
                    etc.combineSplit(0, parameters, " "));
            caller.notify(Colors.Gold + "Results: " + Colors.White
                          + etc.combineSplit(0, results, ", "));
        }
    };
    @Command
    public static final BaseCommand download = new BaseCommand(
            "[plugin] - Downloads a plugin",
            "Usage: /" + EggFinder.PREFIX + " download [plugin]", 1, 1) {

        @Override
        public void execute(final MessageReceiver caller,
                            final String[] parameters) {
            new Thread("Downloader") {
                @Override
                public void run() {
                    String name = etc.combineSplit(0, parameters, " ");
                    caller.notify(Colors.Gray + "Starting download...");
                    if (Downloader.downloadPlugin(name, false))
                        caller.notify(Colors.LightGreen
                                      + "Finished downloading " + name + ".");
                    else {
                        caller.notify("Something went wrong while downloading "
                                      + name + ".");
                        caller.notify("You may want to check your console.");
                    }
                }
            }.start();
        }
    };
    @Command({"install", "update"})
    public static final BaseCommand install = new BaseCommand(
            "[plugin] - Installs the given plugin",
            "Usage: /" + EggFinder.PREFIX + " install [plugin]", 1, 1) {

        @Override
        public void execute(final MessageReceiver caller,
                            final String[] parameters) {
            new Thread("Downloader") {
                @Override
                public void run() {
                    String name = etc.combineSplit(0, parameters, " ");
                    caller.notify(Colors.Gray + "Starting download...");
                    if (Downloader.downloadPlugin(name, true))
                        caller.notify(Colors.LightGreen
                                      + "Finished installing " + name + ".");
                    else {
                        caller.notify("Something went wrong while installing "
                                      + name + ".");
                        caller.notify("You may want to check your console.");
                    }
                }
            }.start();
        }
    };
    @Command
    public static final BaseCommand info = new BaseCommand(
            "[plugin] - Shows info about the plugin",
            "Usage: /" + EggFinder.PREFIX + " info [plugin]", 1, 1) {

        @Override
        public void execute(MessageReceiver caller, String[] parameters) {
            caller.notify(Colors.Gold
                    + String.format("[%s] %s version %s by %s (%s) (size: %s)",
                        (Object[]) Downloader.getPluginInfo(etc.combineSplit(0,
                            parameters, " "))));
        }
    };
}
