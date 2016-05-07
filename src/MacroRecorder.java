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

        if(ControlyUtility.OSName.contains("Windows"))
            key = checkKeyCodeWindows(key);
        else
            key = checkKeyCodeMac(key);


        if (withTimer) {
            temp = new MacroKeyRecord((System.nanoTime() / 1000000 - startTime), key, type);
        } else {
            temp = new MacroKeyRecord(key, type);
        }
        macro.add(temp);

    }

    private static int checkKeyCodeMac(int key){
        switch (key) {
            case 0:
                return 65; //"a";
            case 1:
                return 83; //"s";
            case 2:
                return 68; //"d";
            case 3:
                return 70; //"f";
            case 4:
                return 72; // "h";
            case 5:
                return 71; //"g";
            case 6:
                return 90; //"z";
            case 7:
                return 88; //"x";
            case 8:
                return 67; //"c";
            case 9:
                return 86; //"v";
            case 11:
                return 66; //"b";
            case 12:
                return 81; //"q";
            case 13:
                return 87; //"w";
            case 14:
                return 69; //"e";
            case 15:
                return 82; //"r";
            case 16:
                return 89; //"y";
            case 17:
                return 84; //"t";
            case 18:
                return 49; //"1";
            case 19:
                return 50; //"2";
            case 20:
                return 51; //"3";
            case 21:
                return 52; //"4";
            case 22:
                return 54; //"6";
            case 23:
                return 53; //"5";
            case 24:
                return 61; //"=";
            case 25:
                return 57; //"9";
            case 26:
                return 55; //"7";
            case 27:
                return 45; //"-";
            case 28:
                return 56; //"8";
            case 29:
                return 48; //"0";
            case 30:
                return 93; //"]";
            case 31:
                return 79; //"o";
            case 32:
                return 85; //"u";
            case 33:
                return 91; //"[";
            case 34:
                return 73; //"i";
            case 35:
                return 80; //"p";
            case 37:
                return 76; //"l";
            case 38:
                return 74; //"j";
            case 39:
                return 222; //"'";
            case 40:
                return 75; //"k";
            case 41:
                return 59; //";";
            case 42:
                return 92; //"\\";
            case 43:
                return 44; //",";
            case 44:
                return 47; //"/";
            case 45:
                return 78; //"n";
            case 46:
                return 77; //"m";
            case 47:
                return 46; //".";
            case 50:
                return 192; //"`";
            case 65:
                return 110; //"[decimal]";
            case 67:
                return 106;  //"[asterisk]";
            case 69:
                return 107; //"[plus]";
            case 71:
                return 144; //"[clear]";
            case 75:
                return 111; //"[divide]";
            case 76:
                return 10; //"[enter]";
            case 78:
                return 109; //"[hyphen]";
            case 81:
                return 61; //"[equals]";
            case 82:
                return 96; //"0";
            case 83:
                return 97; //"1";
            case 84:
                return 98; //"2";
            case 85:
                return 99; //"3";
            case 86:
                return 100; //"4";
            case 87:
                return 101; //"5";
            case 88:
                return 102; //"6";
            case 89:
                return 103; //"7";
            case 91:
                return 104; //"8";
            case 92:
                return 105; //"9";
            case 36:
                return 10; //"[return]";
            case 48:
                return 9; //"[tab]";
            case 49:
                return 32; //" ";
            case 51:
                return 8; //"[del]";
            case 53:
                return 27; //"[esc]";
            case 55:
                return 157; //"[cmd]";
            case 60:
            case 56:
                return 16; //"[shift]";
            case 57:
                return 20; //"[caps]";
            case 61:
            case 58:
                return 18; //"[option]";
            case 62:
            case 59:
                return 17; //"[ctrl]";
            case 96:
                return 116; //"[f5]";
            case 97:
                return 117; //"[f6]";
            case 98:
                return 118; //"[f7]";
            case 99:
                return 114; //"[f3]";
            case 100:
                return 119; //"[f8]";
            case 101:
                return 120; //"[f9]";
            case 103:
                return 122; //"[f11]";
            case 109: 
                return 121; //"[f10]";
            case 111:
                return 123; //"[f12]";
            case 114:
                return 156; //"[help]";
            case 115:
                return 36; //"[home]";
            case 116:
                return 33; //"[pgup]";
            case 117:
                return 127; //"[fwddel]";
            case 118:
                return 115; //"[f4]";
            case 119:
                return 35; //"[end]";
            case 120:
                return 113; //"[f2]";
            case 121:
                return 34; //"[pgdown]";
            case 122:
                return 112; //"[f1]";
            case 123:
                return 37; //"[left]";
            case 124:
                return 39; //"[right]";
            case 125:
                return 40; //"[down]";
            case 126:
                return 38; //"[up]";

        }
            return key;
    }

    private static int checkKeyCodeWindows(int key){
        switch(key){


            case 12: //
                key = 101;
                break;
            case 13: // enter key
                key = 10;
                break;
            case 44: // print screen key
                key = 154;
                break;
            case 46: // DELETE key
                key = 127;
                break;
            case 47: // HELP key
                key = 156;
                break;
            case 91: // left win key
            case 92: // right win key
                key = 524;
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
            case 186: // SEMICOLON ;
                key = 59;
                break;
            case 187: // PLUS (+)
                key = 61;
                break;
            case 188: // COMMA (,<)
                key = 44;
                break;
            case 189: // MINUS (-)
                key = 45;
                break;
            case 190: // (.>)
                key = 46;
                break;
            case 191: // (?/)
                key = 47;
                break;
            case 219: // { open bracket
                key = 91;
                break;
            case 220: // BACKSLASH (\|)
                key = 92;
                break;
            case 221: // } close bracket
                key = 93;
                break;


        }
        return key;

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
        LOGGER.info("Macro before format: " + macro);
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
        LOGGER.info("Macro after format: " + finalMacro);
        return finalMacro;
    }

    public void finishMacro() {
        synchronized (this) {
            this.notifyAll();
        }

    }
}
