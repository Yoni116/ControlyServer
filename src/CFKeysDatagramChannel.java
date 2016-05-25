import java.awt.*;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class CFKeysDatagramChannel extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private DatagramChannel channel;
    private HashSet<CFClient> clients;
    private ConcurrentHashMap<String, KeyPress> pressedKeys = new ConcurrentHashMap<String, KeyPress>();
    private ExecutorService executor;
    private Robot robot;
    private SocketAddress clientAddress;
    private boolean isRunning;

    public CFKeysDatagramChannel(DatagramChannel c, HashSet<CFClient> clients) {
        isRunning = true;
        channel = c;
        this.clients = clients;
        executor = Executors.newFixedThreadPool(5);
        try {
            robot = new Robot();
            robot.setAutoWaitForIdle(true);
            robot.setAutoDelay(1);
        } catch (AWTException e) {
            LOGGER.warning(e.getMessage());
        }

    }

    public DatagramChannel getChannel() {
        return channel;
    }


    public void run() {

        try {

            LOGGER.info("Keys Ready");
            ByteBuffer buff = ByteBuffer.allocate(10240);
            Charset charSet = Charset.forName("UTF-8");
            CharsetDecoder coder = charSet.newDecoder();
            CharBuffer charBuff;


            while (isRunning) {

//waiting for msg to arrive
                buff.clear();
                //  System.out.println("Keys waiting");
                clientAddress = channel.receive(buff);
                //  System.out.println("Keys got msg");

                buff.flip();

                charBuff = coder.decode(buff);

                String temp = charBuff.toString().trim();

                String[] splitedMsg = temp.split(":");

                switch (splitedMsg[0]) {

                    case "key":
                        String result = splitedMsg[1];
                        LOGGER.info("Received Key: " + splitedMsg[1] + " From: " + clientAddress);

                        if (result != null) {
                            final String command = result.substring(2);
                            if (pressedKeys.containsKey(command)) {
                                LOGGER.info("extends " + command + " From: " + clientAddress);
                                if (pressedKeys.get(command) != null)
                                    pressedKeys.get(command).extendDeletion();
                            } else {
                                LOGGER.info("pressing " + command + " From: " + clientAddress);
                                Runnable key = new KeyPress(command, pressedKeys, robot);
                                executor.submit(key);
                                pressedKeys.put(command, (KeyPress) key);
                            }


                        } else {
                            LOGGER.warning("Received a null key");
                        }

                        break;
                    case "macro":
                        LOGGER.info("Received Macro: " + splitedMsg[3] + " in Mode: "+splitedMsg[2] +" From: " + clientAddress);
                        CFClient tempClient = findClient(clientAddress);
                        new Thread(
                                new MacroExecute(splitedMsg[3], Integer.parseInt(splitedMsg[2]),splitedMsg[1],tempClient)).start();
                        break;
                }


                buff.clear();

            }


        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public void endThreadPool() {
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        LOGGER.info("closed all threads");
    }

    public void closeKDC() {
        isRunning = false;
        try {
            channel.close();
        } catch (IOException e) {
            LOGGER.warning("Closing KDC " + e.getMessage());
        }
    }

    public CFClient findClient(SocketAddress add){
        String[] address = add.toString().split(":");
        String newAdd = address[0];
        for (Iterator<CFClient> it = clients.iterator(); it.hasNext(); ) {
            CFClient c = it.next();
            if (c.getIp().equals(newAdd))
                return c;
        }
        return null;
    }


}