import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CFMouseDatagramChannel implements Runnable {

    private DatagramChannel channel;
    private ExecutorService executor;

    public CFMouseDatagramChannel(DatagramChannel c) {
        channel = c;
        executor = Executors.newFixedThreadPool(10);
    }

    public DatagramChannel getChannel() {
        return channel;
    }

    public void run() {

        System.out.println("MousePad Opened");
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
            System.out.println("mouse pos is x: " + x + " y: " + y);

            while (true) {
                //someone is sending us data
                buf.clear();
                //  System.out.println("Keys waiting");
                channel.receive(buf);
                // System.out.println("mouse got smth");
                buf.flip();

                cBuff = coder.decode(buf);
                String result = cBuff.toString().trim();

                //CFMouseMovement cFMouseMovement = new CFMouseMovement(result);
                Runnable mouseCommand = new CFMouseMovement(result);
                executor.execute(mouseCommand);
                //new Thread(cFMouseMovement).start();

                buf.clear();


            }


        } catch (IOException exception) {
            System.out.println("Error: " + exception);
        }
    }

}
