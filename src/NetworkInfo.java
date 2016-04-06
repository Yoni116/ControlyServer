import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Created by yoni on 11/29/2015.
 */
public class NetworkInfo extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    private NetworkInterface ni;
    private InetAddress ia;
    private boolean isRunning;
    private CFService myService;
    private MainFrameFX mfFX;
    private boolean serviceRunning;
    private boolean firstTime;


    public NetworkInfo(CFService myService, MainFrameFX mfFX) {
        this.mfFX = mfFX;
        this.myService = myService;
        this.isRunning = true;
        this.serviceRunning = false;
        ControlyUtility.setInetAddress();
        ia = ControlyUtility.localAddress;
        firstTime = true;
    }

    @Override
    public void run() {

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("networkInfo is ready to run");

        while (isRunning) {

            ControlyUtility.setInetAddress();

            if (ControlyUtility.localAddress != null) {

                if (!serviceRunning) {
                    synchronized (myService) {
                        myService.notifyAll();
                    }
                    ia = ControlyUtility.localAddress;
                    serviceRunning = true;
                }

                InetAddress newAddress = null;

                NetworkInterface newInterface = null;
                try {

                    ni = NetworkInterface.getByInetAddress(ia);
                    newAddress = ControlyUtility.localAddress;
                    newInterface = NetworkInterface.getByInetAddress(newAddress);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                if (!ni.equals(newInterface) || !ia.equals(newAddress)) {
                    LOGGER.info("OLD-address: " + ia + " network: " + ni);
                    LOGGER.info("NEW-address: " + newAddress + " network: " + newInterface);
                    restartService();
                    ni = newInterface;
                    ia = newAddress;
                }
            } else {
                LOGGER.warning("No internet connection present - waiting");
                if (serviceRunning)
                    restartService();
            }
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ia = ControlyUtility.localAddress;
        }


    }

    public void setService(CFService s) {
        myService = s;
    }

    public void closeInfo() {
        isRunning = false;
    }

    public void restartService() {
        try {
            LOGGER.warning("Starting reset to Server - Expect some Exceptions");
            myService.close();
            Thread.sleep(5000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mfFX.resetService();
        serviceRunning = false;


    }


}
