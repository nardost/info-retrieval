package ntessema.csc575.commons;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {
    private static Map<String, String> configuration = null;

    private ConfigurationManager() {
        configuration = new HashMap<>();
        String configurationFile = "configuration.xml";
        try(InputStream in = getClass().getClassLoader().getResourceAsStream(configurationFile)) {
            Document document = (DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in));
            NodeList properties = document.getElementsByTagName("property");
            for(int index = 0; index < properties.getLength(); index++) {
                Element property = (Element) properties.item(index);
                String key = property.getAttribute("key");
                String value = property.getAttribute("value");
                configuration.put(key, value);
            }
        } catch(IOException ioe) {
            System.out.println("Cannot read configuration file " + configurationFile);
        } catch(ParserConfigurationException pce) {
            System.out.println("Document builder cannot be created.");
        } catch(SAXException se) {
            System.out.println("Parsing error. Check for xml syntax errors in the configuration file " + configurationFile);
        }
    }

    /**
     * ConfigurationManager should be a singleton.
     */
    public static void init() {
        if(configuration == null) {
            synchronized (ConfigurationManager.class) {
                if(configuration == null) {
                    new ConfigurationManager();
                }
            }
        }
    }

    public static String getConfiguration(String config) {
        init();
        return configuration.get(config);
    }
}
