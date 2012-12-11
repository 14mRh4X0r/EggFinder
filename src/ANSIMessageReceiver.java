public class ANSIMessageReceiver implements MessageReceiver {
    private final MessageReceiver wrapped;

    public ANSIMessageReceiver(MessageReceiver toWrap) {
        this.wrapped = toWrap;
        
    }

    public String getName() {
        return wrapped.getName();
    }

    public void notify(String message) {
        // Assume ANSI support on non-windows systems
        message = System.getProperty("os.name").startsWith("Windows")
                  ? Colors.strip(message)
                  : Colors.toANSI(message);
        wrapped.notify(message);
    }
}
