import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by yoni on 26/08/2015.
 */
public class MacroRecorder extends Thread {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static Vector<MacroKeyRecord> macro;
    private static int seq;
    private static long startTime;
    private static boolean withTimer;
    public String client;
    public String finalMacro = "macro:";
    private long endTime;


    public MacroRecorder(boolean withTimer, String client) {
        seq = 1;
        this.client = client;
        MacroRecorder.withTimer = withTimer;
        macro = new Vector<>();

    }

    public static void recordKey(int key, int type) {
        MacroKeyRecord temp;
        switch(key){
            case 13: // enter key
                key = '\n';
                break;
            case 160: // left shift
            case 161: // right shift
                key = 16;
                break;
            case 162: // left ctrl
            case 163: // right ctrl
                key = 17;
                break;
            case 164: // left alt
            case 165: // right alt
                key = 18;
                break;
            case 91: // left win key
            case 92: // right win key
                key = 524;
                break;
        }

        if (withTimer) {
            temp = new MacroKeyRecord((System.nanoTime() / 1000000 - startTime), key, type);
        } else {
            temp = new MacroKeyRecord(key, type);
        }
        macro.add(temp);

    }

    public static native void SetHook();

    public static native void ReleaseHook();

    @Override
    public void run() {
        LOGGER.info("Starting Macro Record for client: " + client);
        new Thread(() -> {
            if (withTimer)
                startTime = System.nanoTime() / 1000000;
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


    }

    public void stopRecord() {
        if (withTimer)
            endTime = System.nanoTime() / 1000000;
        ReleaseHook();
    }

    public String buildMacro() {

        if (withTimer) {
            finalMacro = finalMacro.concat("1:");
            boolean found = false;
            while (macro.size() > 0) {
                Iterator<MacroKeyRecord> iterator = macro.iterator();
                MacroKeyRecord current = iterator.next();
                if (current.getKeyUpDown() == 1) {
                    iterator.remove();
                } else {
                    iterator.remove();
                    while (iterator.hasNext()) {
                        MacroKeyRecord temp = iterator.next();
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
            finalMacro = finalMacro.concat("0:");
            MacroKeyRecord lastKey = null;
            HashSet<Integer> temp = new HashSet<>();
            for (MacroKeyRecord key : macro) {

                switch (key.getKeyUpDown()) {
                    case 0:
                        if (!temp.contains(key.getKeyCode())) {
                            temp.add(key.getKeyCode());
                            finalMacro = finalMacro.concat(key.getKeyCode() + "," + key.getKeyUpDown() + ";");
                        }
                        break;
                    case 1:
                        temp.remove(key.getKeyCode());
                        if (!lastKey.equals(key))
                            finalMacro = finalMacro.concat(key.getKeyCode() + "," + key.getKeyUpDown() + ";");
                        break;
                }
                lastKey = key;
            }
        }
        return finalMacro;
    }

    public void finishMacro() {
        synchronized (this) {
            this.notifyAll();
        }

    }
}
