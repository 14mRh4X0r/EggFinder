
import java.util.logging.Logger;

public class EggFinder extends Plugin {
    static final Logger log = Logger.getLogger("Minecraft.PluginFinder");
    private PluginRegisteredListener prl;
    private CommandListener cl = new CommandListener();

    @Override
    public void enable() {
        this.prl = etc.getLoader().addListener(PluginLoader.Hook.COMMAND, cl, this, PluginListener.Priority.MEDIUM);
        log.info(this.getName() + " enabled.");
    }

    @Override
    public void disable() {
        etc.getLoader().removeListener(prl);
        log.info(this.getName() + " disabled.");
    }

}
