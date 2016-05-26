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

    //public static native boolean GetCapsLockState();

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static String OSName = System.getProperty("os.name");

    public static int number = 1;

    public static InetAddress localAddress = null;

    public static String version ="V6.4.5";

 //   public static boolean capsLockState = false;



    public static void setInetAddress() {
        boolean found = false;
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements() && !found) {
                NetworkInterface current = interfaces.nextElement();
                if (!current.isLoopback()) {
                    Enumeration<InetAddress> addresses = current.getInetAddresses();
                    while (addresses.hasMoreElements() && !found) {
                        InetAddress current_addr = addresses.nextElement();
                        if (current_addr.isSiteLocalAddress() && current_addr instanceof Inet4Address && !current_addr.isLoopbackAddress()) {
                            if (!isVmwareMac(current.getHardwareAddress()) && !current.getDisplayName().toLowerCase().contains("virtual") && !current.getDisplayName().toLowerCase().contains("bridge")) {
                                ControlyUtility.localAddress = current_addr;
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (!found)
            ControlyUtility.localAddress = null;
    }

//    public static void initializeVars(){
//        setInetAddress();
//        osName = System.getProperty("os.name");
//        if(osName.contains("Windows"))
//            capsLockState = GetCapsLockState();
//        else
//            capsLockState = false;
//
//    }

//    public static boolean getCapsState(){
//
//        if(osName.contains("Windows")) {
//            boolean tmp = GetCapsLockState();
//            return tmp;
//        }
//        else
//            return false;
//
//    }


    private static boolean isVmwareMac(byte[] mac) {
        byte invalidMacs[][] = {
                {0x00, 0x05, 0x69},             //VMWare
                {0x00, 0x1C, 0x14},             //VMWare
                {0x00, 0x0C, 0x29},             //VMWare
                {0x00, 0x50, 0x56},              //VMWare
                {0x00, 0x1C, 0x42},
                {0x08, 0x00, 0x27},
                {0x10, 0x00, 0x27},
                {0x00, 0x03, (byte) 0xFF},
                {0x00, 0x16, 0x3E}
        };

        for (byte[] invalid : invalidMacs) {
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2])
                return true;
        }

        return false;
    }
}
