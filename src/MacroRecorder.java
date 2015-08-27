import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by yoni on 26/08/2015.
 */
public class MacroRecorder extends Thread {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static Vector<KeyRecord> macro = new Vector<>();
    private static int seq;
    private static long startTime;
    private static boolean withTimer;
    public String client;
    public String finalMacro = "";
    private long endTime;


    public MacroRecorder(boolean withTimer, String client) {
        seq = 1;
        MacroRecorder.withTimer = withTimer;
        this.client = client;

    }

    public static void recordKey(int key, int type) {
        KeyRecord temp;
        // System.out.println(key +" "+type);

        if (withTimer) {
            temp = new KeyRecord((System.nanoTime() / 1000000 - startTime), key, type);
        } else {
            temp = new KeyRecord(key, type);
        }
        macro.add(temp);
    }

    public static native void SetHook();

    public static native void ReleaseHook();

    @Override
    public void run() {
        LOGGER.info("Starting Macro Record For Client: " + client);
        if (withTimer)
            startTime = System.nanoTime() / 1000000;


        new Thread(() -> {
            SetHook();
            System.out.println("here");
        }).start();


        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("Macro Thread Closing");


    }

    public void stopRecord() {
        if (withTimer)
            endTime = System.nanoTime() / 1000000;
        ReleaseHook();
        LOGGER.info("Macro Record Ended");
    }

    public String buildMacro() {

        if (withTimer) {
            boolean found = false;
            while (macro.size() > 0) {
                Iterator<KeyRecord> iterator = macro.iterator();
                KeyRecord current = iterator.next();
                if (current.getKeyUpDown() == 1) {
                    iterator.remove();
                } else {
                    iterator.remove();
                    while (iterator.hasNext()) {
                        KeyRecord temp = iterator.next();
                        if (temp.getKeyCode() == current.getKeyCode()) {
                            if (temp.getKeyUpDown() == 0)
                                iterator.remove();
                            else {
                                found = true;
                                finalMacro = finalMacro.concat(current.getTime() + "," + current.getKeyCode() + "," + (temp.getTime() - current.getTime()) + ";");
                                iterator.remove();
                                break;
                            }
                        }
                    }
                    if (!found)
                        finalMacro = finalMacro.concat(current.getTime() + "," + current.getKeyCode() + "," + (endTime - startTime - current.getTime()) + ";");
                    found = false;
                }

            }

        } else {
            HashSet<Integer> temp = new HashSet<>();
            for (KeyRecord key : macro) {
                switch (key.getKeyUpDown()) {
                    case 0:
                        if (!temp.contains(key.getKeyCode())) {
                            temp.add(key.getKeyCode());
                            finalMacro = finalMacro.concat(key.getKeyCode() + "," + key.getKeyUpDown() + ";");
                        }
                        break;
                    case 1:
                        temp.remove(key.getKeyCode());
                        finalMacro = finalMacro.concat(key.getKeyCode() + "," + key.getKeyUpDown() + ";");
                        break;
                }
            }
        }
        LOGGER.info("Finished Building Macro String For Client: " + client);
        return finalMacro;
    }

    public void finishMacro() {

        synchronized (this) {
            this.notifyAll();
        }

    }
}
