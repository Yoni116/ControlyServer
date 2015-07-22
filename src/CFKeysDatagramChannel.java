import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CFKeysDatagramChannel implements Runnable {


    private DatagramChannel channel;
    private ConcurrentHashMap<String, KeyPress> pressedKeys = new ConcurrentHashMap<String, KeyPress>();
    private ExecutorService executor;
    private Robot robot;

    public CFKeysDatagramChannel(DatagramChannel c) {
        channel = c;
        executor = Executors.newFixedThreadPool(5);
        try {
            robot = new Robot();
            robot.setAutoWaitForIdle(true);
            robot.setAutoDelay(1);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    public DatagramChannel getChannel() {
        return channel;
    }


    public void run() {

        try {

            System.out.println("Keys Ready");
            ByteBuffer buff = ByteBuffer.allocate(48);
            Charset charSet = Charset.forName("UTF-8");
            CharsetDecoder coder = charSet.newDecoder();
            CharBuffer charBuff;


            while (true) {

//waiting for msg to arrive
                buff.clear();
                //  System.out.println("Keys waiting");
                channel.receive(buff);
                //  System.out.println("Keys got msg");
                buff.flip();

                charBuff = coder.decode(buff);
                String result = charBuff.toString().trim();

                System.out.println("this is: " + result);

                if (result != null) {
                    final String command = result.substring(2);
                    if (pressedKeys.containsKey(command)) {
                        System.out.println("extends " + command);
                        if (pressedKeys.get(command) != null)
                            pressedKeys.get(command).extendDeletion();
                    } else {
                        Runnable key = new KeyPress(command, pressedKeys, robot);
                        executor.submit(key);
                        pressedKeys.put(command, (KeyPress) key);
                    }


                } else {
                    System.out.println("Received a null key");
                }

                buff.clear();

            }


        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void endThreadPool() {
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("closed all threads");
    }


}