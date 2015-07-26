import aurelienribon.tweenengine.TweenManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yoni on 24/07/2015.
 */
public class newMainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final int FRAME_SIZE = Math.min((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 3;
    private final int BUTTON_SIZE = 30;

    private JPanel contentPane;


    private JButton minimizeButton;
    private JButton settingButton;
    private JButton exitButton;
    private CFService service;
    private JLabel backbgroundImage;
    private PopupMenu popup;
    private TrayIcon trayIcon;
    private SystemTray tray = SystemTray.getSystemTray();
    private MenuItem showApp;
    private MenuItem closeApp;
    private boolean running;


    public newMainFrame() {

        setSize(FRAME_SIZE, FRAME_SIZE);
        setTitle("Controly");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);
        setUndecorated(true);
        LOGGER.info("" + Toolkit.getDefaultToolkit().getScreenSize().getHeight());

        contentPane = new JPanel();
        contentPane.setLayout(null);

        minimizeButton = new JButton("");
        Image bimg = new ImageIcon(newMainFrame.class.getResource("/NewServerDesign/MinimizeButton.png"))
                .getImage().getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        minimizeButton.setIcon(new ImageIcon(bimg));
        minimizeButton.setBounds(FRAME_SIZE - BUTTON_SIZE - 15, 10, BUTTON_SIZE, BUTTON_SIZE);
        minimizeButton.setOpaque(false);
        minimizeButton.setContentAreaFilled(false);
        minimizeButton.setBorderPainted(false);
        minimizeButton.addActionListener(this);
        contentPane.add(minimizeButton);

        settingButton = new JButton("");
        bimg = new ImageIcon(newMainFrame.class.getResource("/NewServerDesign/SettingButton.png"))
                .getImage().getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        settingButton.setIcon(new ImageIcon(bimg));
        settingButton.setBounds(15, 10, BUTTON_SIZE, BUTTON_SIZE);
        settingButton.setOpaque(false);
        settingButton.setContentAreaFilled(false);
        settingButton.setBorderPainted(false);
        settingButton.addActionListener(this);
        contentPane.add(settingButton);


        backbgroundImage = new JLabel("");
        bimg = new ImageIcon(newMainFrame.class.getResource("/NewServerDesign/MainBackground.png"))
                .getImage().getScaledInstance(FRAME_SIZE, FRAME_SIZE, Image.SCALE_SMOOTH);
        backbgroundImage.setIcon(new ImageIcon(bimg));
        backbgroundImage.setBounds(0, 0, FRAME_SIZE, FRAME_SIZE);
        contentPane.add(backbgroundImage);


        setContentPane(contentPane);
        setAlwaysOnTop(true);
        setVisible(true);
        startServer();
    }

    public void startServer() {
        LOGGER.info("Start");
        if (service == null) { // run server if not running
            try {

                service = new CFService();
                service.start();
                //ipNum = service.getIP();
                //portNum = service.getPort();


            } catch (IOException e1) {
                LOGGER.log(Level.SEVERE, e1.toString(), e1);
                //e1.printStackTrace();
            }

            //we should register a server.
        } else { // if pressed again stop service and defualt the vars

            //closeService();
            // ipNum = "Start Server First";
            //portNum = 0;
            service = null;
            LOGGER.info("Stop");


        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
