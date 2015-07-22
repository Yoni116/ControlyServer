import javax.swing.*;
import java.awt.*;

/**
 * Created by yoni on 05/02/2015.
 * creates a Frame that show connection info (ip and port)
 * only visible when server is running
 */
public class ConnectionFrame extends Thread {


    private JFrame frame;
    private JPanel mainPanel;
    private JLabel mainLabel;
    private String ip = "123.456.789";
    private int port = 1234;
    private JLabel ipLabel;
    private JLabel portLabel;
    private boolean isRuning;
    private boolean isShown = false;

    public ConnectionFrame(int x, int y) {

        frame = new JFrame();
        mainPanel = new JPanel();
        mainLabel = new JLabel();
        ipLabel = new JLabel(ip);
        portLabel = new JLabel(String.valueOf(port));

        frame.setType(Window.Type.UTILITY);
        frame.setSize(218, 133);
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setLocationRelativeTo(null);


        mainPanel.setOpaque(false);
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        mainPanel.setLayout(null);
        frame.setContentPane(mainPanel);

        mainLabel.setIcon(new ImageIcon(ConnectionFrame.class.getResource("/Resources/detailsLable/DetailsLable.png")));
        mainLabel.setOpaque(true);
        mainLabel.setBounds(0, 0, 218, 133);
        mainLabel.setBackground(new Color(0, 0, 0, 0));
        ipLabel.setBounds(85, 20, 100, 50);
        ipLabel.setForeground(Color.WHITE);
        ipLabel.setFont(new Font("Serif", Font.BOLD, 14));

        portLabel.setBounds(108, 60, 100, 50);
        portLabel.setForeground(Color.WHITE);
        portLabel.setFont(new Font("Serif", Font.BOLD, 14));
        mainLabel.add(ipLabel);
        mainLabel.add(portLabel);


        frame.add(mainLabel);
        //GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        frame.setLocation(x - 225, y);
        frame.setAlwaysOnTop(true);

    }

    public void showFrame() {
        frame.setVisible(true);
        isShown = true;
    }


    public void hideFrame() {
        frame.setVisible(false);
        isShown = false;
    }

    public boolean isShown() {
        return isShown;

    }


    @Override
    public void run() {
        isRuning = true;

        while (isRuning) {


            synchronized (this) {
                try {
                    showFrame();
                    this.wait();
                    hideFrame();
                    this.wait();

                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }


        }
        frame.dispose();
    }

    public void setIpPort(String ipNum, int portNum) {
        ipLabel.setText(ipNum);
        portLabel.setText(String.valueOf(portNum));


    }

    public void closeThread() {
        isRuning = false;
        this.interrupt();
    }
}
