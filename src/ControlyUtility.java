import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * Created by yoni on 09/09/2015.
 */
public class ControlyUtility {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static String OSName = System.getProperty("os.name");

    public static int number = 1;

    public static InetAddress getInetAddress(){
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()){
                NetworkInterface current = interfaces.nextElement();
                if(!current.isLoopback() ){
                    Enumeration<InetAddress> addresses = current.getInetAddresses();
                    while (addresses.hasMoreElements()){
                        InetAddress current_addr = addresses.nextElement();
                        if (current_addr.isSiteLocalAddress() && current_addr instanceof Inet4Address && !current_addr.isLoopbackAddress()) {
                            if(!isVmwareMac(current.getHardwareAddress()))
                                return current_addr;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static InetAddress getInetAddress() throws RuntimeException{
//
//        try {
//            return Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
//                    .flatMap(i -> Collections.list(i.getInetAddresses()).stream())
//                    .filter(ip -> ip instanceof Inet4Address && ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !isVmwareMac(ip.get))
//                    .findFirst().orElseThrow(RuntimeException::new);
//        } catch (SocketException e) {
//            LOGGER.severe(e.getMessage());
//        }
//        return null;
//    }

    public static void setCapsLockFalse(){
        Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK,false);

    }

    private static boolean isVmwareMac(byte[] mac) {
        byte invalidMacs[][] = {
                {0x00, 0x05, 0x69},             //VMWare
                {0x00, 0x1C, 0x14},             //VMWare
                {0x00, 0x0C, 0x29},             //VMWare
                {0x00, 0x50, 0x56}              //VMWare
        };

        for (byte[] invalid: invalidMacs){
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2])
                return true;
        }

        return false;
    }
}
