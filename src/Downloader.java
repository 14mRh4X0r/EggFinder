
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Downloader {

    private static Document parseSearch(String search) {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return db.parse("http://www.canarymod.net/plugins/xml.php?name=" + URLEncoder.encode(search, "UTF-8"));
        } catch (Exception ex) {
            EggFinder.LOG.log(Level.SEVERE, "Something went wrong while parsing the search results", ex);
        }
        return null;
    }

    private static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String[] getSearchResults(String search) {
        Document document = parseSearch(search);
        if (document == null)
            return null;
        NodeList list = document.getElementsByTagName("title");

        String[] res = new String[list.getLength()];
        for (int i = 0; i < list.getLength(); i++) {
            res[i] = list.item(i).getTextContent();
        }

        return res;
    }

    /*
     * {category, title, version, author, cbuild, size}
     */
    public static String[] getPluginInfo(String pluginName) {
        Document document = parseSearch(pluginName);
        if (document == null)
            return null;
        NodeList list = document.getElementsByTagName("plugin");
        if (list.getLength() > 1) {
            return null;
        }
        Element plugin = (Element) list.item(0);

        String[] info = new String[6];
        info[0] = plugin.getElementsByTagName("category").item(0).getTextContent();
        info[1] = plugin.getElementsByTagName("title").item(0).getTextContent();
        info[2] = plugin.getElementsByTagName("version").item(0).getTextContent();
        info[3] = plugin.getElementsByTagName("author").item(0).getTextContent();
        info[4] = plugin.getElementsByTagName("cbuild").item(0).getTextContent();
        info[5] = readableFileSize(Long.parseLong(
                plugin.getElementsByTagName("size").item(0).getTextContent()));

        return info;
    }
    
    public static boolean downloadPlugin(String pluginName, boolean install) {
        Document document = parseSearch(pluginName);
        if (document == null)
            return false;
        NodeList list = document.getElementsByTagName("plugin");
        if (list.getLength() > 1)
            return false;
        Element plugin = (Element) list.item(0);
        
        String category = plugin.getElementsByTagName("category").item(0).getTextContent();
        String id = plugin.getElementsByTagName("id").item(0).getTextContent();
        String title = plugin.getElementsByTagName("title").item(0).getTextContent();
        
        try {
            Main.downloadFile("http://dl.canarymod.net/plugins/get.php?c="
                    + category.toLowerCase() + "&id=" + id, "plugins/" + title + ".jar");
        } catch (IOException ex) {
            EggFinder.LOG.log(Level.SEVERE, "Something went wrong while downloading the plugin", ex);
        }
        
        if (install) {
            if (!etc.getLoader().reloadPlugin(title))
                return false;
            else {
                PropertiesFile f = new PropertiesFile("server.properties");
                f.setString("plugins", f.getString("plugins") + "," + title);
                f.save();
            }
        }
        return true;
    }
}
