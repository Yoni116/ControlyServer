import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by yoni on 09/09/2015.
 */
public class ControlyUtility {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    public static InetAddress getInetAddress() {

        try {
            return Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                    .flatMap(i -> Collections.list(i.getInetAddresses()).stream())
                    .filter(ip -> ip instanceof Inet4Address && ip.isSiteLocalAddress())
                    .findFirst().orElseThrow(RuntimeException::new);
        } catch (SocketException e) {
            LOGGER.severe(e.getMessage());
        }
        return null;
    }

    public static void setCapsLockFalse(){
        Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK,false);

    }
}
