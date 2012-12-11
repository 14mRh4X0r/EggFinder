import java.util.logging.Logger;

public class EggFinder extends Plugin {
    public static final String VERSION = "2.0";
    public static final String PREFIX = "finder";
    public static final String NAME = EggFinder.class.getSimpleName();
    public static final String AUTHOR = "14mRh4X0r";
    private static final FinderCommands FC = new FinderCommands();
    static final Logger LOG = Logger.getLogger("Minecraft.EggFinder");
    private static final BaseCommand COMMAND = new BaseCommand(
            "[subcommand] - See /help " + PREFIX, "Overridden", 2) {

        @Override
        protected void execute(final MessageReceiver caller,
                               String[] parameters) {
            FC.parseCommand(new ANSIMessageReceiver(caller), parameters);
        }

        @Override
        public void onBadSyntax(final MessageReceiver caller,
                                String[] parameters) {
            // Show help on subcommands
            PlayerCommands.parsePlayerCommand(new ANSIMessageReceiver(caller),
                    "help", new String[]{ "help", PREFIX });
        }
    };

    static {
        // Set prefix (the fancy hackish way)
        LOG.setFilter(new java.util.logging.Filter() {
            @Override
            public boolean isLoggable(java.util.logging.LogRecord record) {
                record.setMessage("[" + NAME + "] " + record.getMessage());
                return true;
            }
        });
    }

    @Override
    public void enable() {
        ServerConsoleCommands.getInstance().add(PREFIX, COMMAND);
        LOG.info("Version " + VERSION + " by " + AUTHOR + " enabled.");
    }

    @Override
    public void disable() {
        if (ServerConsoleCommands.getInstance().getCommand(PREFIX) == COMMAND)
            ServerConsoleCommands.getInstance().remove(PREFIX);

        LOG.info("Plugin disabled.");
    }

}
