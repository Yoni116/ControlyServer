import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import javax.swing.*;
import java.awt.*;

public class CFPopup extends JFrame implements Runnable {

    private String ipNum;
    private int portNum;
    private int _timeSlice = 50; // update every 50 milliseconds
    private Timer _timer;
    public TweenManager manager;

    public CFPopup(String ip, int port, TweenManager tweenManager) {
        ipNum = ip;
        portNum = port;
        manager = tweenManager;
        setSize(321, 89);
        // setBounds(-321, 0, 321, 89);
        setTitle("Project-X");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0));
        // setOpacity(0);
        // setResizable(false); //frame jumps because of this command
        setLocationRelativeTo(null);
        JPanel contentPane = new JPanel();
        contentPane.setOpaque(false);
        contentPane.setBackground(new Color(0, 0, 0, 0));
        // contentPane.setBackground(new Color(51, 204, 255));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel deviceName = new JLabel(ipNum + ":" + portNum);
        deviceName.setForeground(Color.WHITE);
        deviceName.setBounds(99, 51, 135, 16);
        contentPane.add(deviceName);

        JLabel backgroundImage = new JLabel("");
        backgroundImage.setOpaque(true);
        backgroundImage
                .setIcon(new ImageIcon(
                        CFPopup.class
                                .getResource("/Resources/TabletConnectedPlugged.png")));
        backgroundImage.setBounds(0, 0, 321, 89);
        backgroundImage.setBackground(new Color(0, 0, 0, 0));
        contentPane.add(backgroundImage);
        setLocation(-321, 100);
        setVisible(true);

        // System.out.println("bounds: " + getBounds());


    }

    static public void incoming(String ip, int port, TweenManager manager) {
        CFPopup popup = new CFPopup(ip, port, manager);
        popup.run();
    }

    public void setX(float f) {
        // TODO Auto-generated method stub
        //System.out.println(f);
        setLocation((int) f, getLocation().y);
    }

    public void setY(float f) {
        // TODO Auto-generated method stub
        setLocation(getLocation().x, (int) f);

    }

    private void close() {
        this.dispose();
    }


    @Override
    public void run() {
        Tween.registerAccessor(CFPopup.class, new CFPopupAccessor());
        Timeline.createSequence()
                .push(Tween.to(this, CFPopupAccessor.POSITION_X, 0.5f).target(0))
                .pushPause(5.0f)
                .push(Tween.to(this, CFPopupAccessor.POSITION_X, 0.5f).target(-321))
                .start(this.manager);

        try {
            Thread.currentThread().sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        close();

    }

}
