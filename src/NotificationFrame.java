import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Created by yoni on 02/08/2015.
 */
public class NotificationFrame extends JFrame implements Runnable {


    private static int nextLocation = ((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 4) / 4;
    private final int FRAME_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 4;
    private final int FRAME_HEIGHT = FRAME_WIDTH / 4;
    private float s = 1.0f;

    private JFrame frame;

    private Image bimg;
    private JLabel background;
    private JLabel clientNameLabel;

    private int type;
    //private String clientName;


    public NotificationFrame(String name, int type) {
        this.type = type;
        frame = this;
        setUndecorated(true);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - FRAME_WIDTH - 20, nextLocation);
        setType(javax.swing.JFrame.Type.UTILITY);
        if (nextLocation == FRAME_HEIGHT)
            nextLocation *= 2.1;
        else
            nextLocation = FRAME_HEIGHT;


        background = new JLabel();
       switch(type) {
           case 0:
            bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/ConnectionTile.png"))
                    .getImage().getScaledInstance(FRAME_WIDTH, FRAME_HEIGHT, Image.SCALE_SMOOTH);
               break;
           case 1:
            bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/RecordingStarted.png"))
                    .getImage().getScaledInstance(FRAME_WIDTH, FRAME_HEIGHT, Image.SCALE_SMOOTH);
               break;
           case 2:
               bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/RecordingFinished.png"))
                       .getImage().getScaledInstance(FRAME_WIDTH, FRAME_HEIGHT, Image.SCALE_SMOOTH);
               break;
        }

        background.setIcon(new ImageIcon(bimg));
        background.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);

        clientNameLabel = new JLabel(name);
        clientNameLabel.setForeground(Color.WHITE);
        clientNameLabel.setFont(MainFrame.font);
        clientNameLabel.setBounds(FRAME_WIDTH / 5, FRAME_HEIGHT / 2, FRAME_WIDTH / 3, FRAME_HEIGHT / 3);

        background.add(clientNameLabel);
        add(background);

    }

    @Override
    public void run() {

        this.setVisible(true);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Timer(20, e -> {
            changeOp();
            if (s <= 0) {
                ((Timer) e.getSource()).stop();
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                nextLocation = FRAME_HEIGHT;
            }
        }).start();

    }

    public void changeOp() {
        this.setOpacity(s);
        s -= 0.01f;

    }
}

