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


    public NetworkInfo(InetAddress ia, CFService myService, MainFrameFX mfFX) throws SocketException {
        this.ia = ia;
        this.ni = NetworkInterface.getByInetAddress(ia);
        this.mfFX = mfFX;
        this.myService = myService;
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("networkInfo is ready to run");

        while (isRunning) {

            InetAddress newAddress = null;

            NetworkInterface newInterface = null;
            try {
                newAddress = ControlyUtility.getInetAddress();
                newInterface = NetworkInterface.getByInetAddress(newAddress);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (RuntimeException e){
                restartService();
                ni = newInterface;
                ia = newAddress;
            }

            if (!ni.equals(newInterface) || !ia.equals(newAddress)) {
                LOGGER.info("OLD-address: "+ ia +" network: "+ni);
                LOGGER.info("NEW-address: "+ newAddress +" network: "+newInterface);
                restartService();
                ni = newInterface;
                ia = newAddress;

            }
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void setService(CFService s) {
        myService = s;
    }

    public void closeInfo() {
        isRunning = false;
    }

    public void restartService(){
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


    }


}
