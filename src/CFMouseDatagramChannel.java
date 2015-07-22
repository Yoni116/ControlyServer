import java.awt.*;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CFMouseDatagramChannel implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private DatagramChannel channel;
    private ExecutorService executor;
    private Robot mouse;
    private SocketAddress clientAddress;

    public CFMouseDatagramChannel(DatagramChannel c) {
        channel = c;
        executor = Executors.newFixedThreadPool(20);
        try {
            mouse = new Robot();
            mouse.setAutoWaitForIdle(true);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    public DatagramChannel getChannel() {
        return channel;
    }

    public void run() {

        LOGGER.info("MousePad Opened");
        ByteBuffer buf = ByteBuffer.allocate(48);

        Charset charSmth = Charset.forName("UTF-8");
        CharsetDecoder coder = charSmth.newDecoder();
        CharBuffer cBuff;

        //We need a try-catch because lots of errors can be thrown
        try {

            //get mouse starting position
            Point mousePoint = MouseInfo.getPointerInfo().getLocation();
            double x = mousePoint.getX();
            double y = mousePoint.getY();
            LOGGER.info("mouse pos is x: " + x + " y: " + y);

            while (true) {
                //someone is sending us data
                buf.clear();
                //  System.out.println("Keys waiting");
                clientAddress = channel.receive(buf);
                // System.out.println("mouse got smth");
                buf.flip();

                cBuff = coder.decode(buf);
                String result = cBuff.toString().trim();

                LOGGER.info("Received Command: " + result + " From: " + clientAddress);
                Runnable mouseCommand = new CFMouseMovement(result, mouse);
                executor.submit(mouseCommand);
                //new Thread(cFMouseMovement).start();

                buf.clear();


            }


        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, exception.toString(), exception);
        }
    }

}
