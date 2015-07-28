import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by yoni on 24/07/2015.
 */
public class MainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    private int frameSize = Math.min((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2;

    private int buttonSize = (int) (frameSize * 0.08);

    private JPanel mainPane;
    private JPanel settingPane;
    private Container container;
    private CardLayout cardLayout;

    private JButton minimizeButtonMain;
    private JButton minimizeButtonSetting;
    private JButton settingButton;
    private JButton backButton;
    private CFService service;
    private JLabel settingImage;
    private JLabel backbgroundImage;
    private JLabel ipLabel;
    private JLabel portLabel;
    private PopupMenu popup;

    private SystemTray tray = SystemTray.getSystemTray();
    private MenuItem aboutItem;
    private MenuItem showApp;
    private MenuItem closeApp;
    private boolean running;


    public TrayIcon trayIcon;


    public MainFrame() {


        setSize(frameSize, frameSize);

        container = getContentPane();
        cardLayout = new CardLayout();
        container.setLayout(cardLayout);
        setTitle("Controly");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);
        setUndecorated(true);
        LOGGER.info("" + Toolkit.getDefaultToolkit().getScreenSize().getHeight());

        mainPane = new JPanel();
        mainPane.setLayout(null);

        settingPane = new JPanel();
        settingPane.setLayout(null);

        minimizeButtonMain = new JButton("");
        Image bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/MinimizeButton.png"))
                .getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
        minimizeButtonMain.setIcon(new ImageIcon(bimg));
        minimizeButtonMain.setBounds(frameSize - buttonSize - 15, 10, buttonSize, buttonSize);
        minimizeButtonMain.setOpaque(false);
        minimizeButtonMain.setContentAreaFilled(false);
        minimizeButtonMain.setBorderPainted(false);
        minimizeButtonMain.addActionListener(this);
        mainPane.add(minimizeButtonMain);

        minimizeButtonSetting = new JButton("");
        bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/MinimizeButton.png"))
                .getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
        minimizeButtonSetting.setIcon(new ImageIcon(bimg));
        minimizeButtonSetting.setBounds(frameSize - buttonSize - 15, 10, buttonSize, buttonSize);
        minimizeButtonSetting.setOpaque(false);
        minimizeButtonSetting.setContentAreaFilled(false);
        minimizeButtonSetting.setBorderPainted(false);
        minimizeButtonSetting.addActionListener(this);
        settingPane.add(minimizeButtonSetting);


        settingButton = new JButton("");
        bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/SettingButton.png"))
                .getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
        settingButton.setIcon(new ImageIcon(bimg));
        settingButton.setBounds(15, 10, buttonSize, buttonSize);
        settingButton.setOpaque(false);
        settingButton.setContentAreaFilled(false);
        settingButton.setBorderPainted(false);
        settingButton.addActionListener(this);
        mainPane.add(settingButton);

        backButton = new JButton("");
        bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/LeftArrowButton.png"))
                .getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
        backButton.setIcon(new ImageIcon(bimg));
        backButton.setBounds(15, 10, buttonSize, buttonSize);
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(this);
        settingPane.add(backButton);


        backbgroundImage = new JLabel("");
        bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/MainBackground.png"))
                .getImage().getScaledInstance(frameSize, frameSize, Image.SCALE_SMOOTH);
        backbgroundImage.setIcon(new ImageIcon(bimg));
        backbgroundImage.setBounds(0, 0, frameSize, frameSize);
        mainPane.add(backbgroundImage);

        settingImage = new JLabel("");
        bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/SettingBackground.png"))
                .getImage().getScaledInstance(frameSize, frameSize, Image.SCALE_SMOOTH);
        settingImage.setIcon(new ImageIcon(bimg));
        settingImage.setBounds(0, 0, frameSize, frameSize);
        settingPane.add(settingImage);

        ipLabel = new JLabel("Test");
        ipLabel.setBounds(185, 205, 250, 50);
        ipLabel.setForeground(Color.WHITE);
        ipLabel.setFont(new Font("Open Sans", Font.BOLD, 30));


        portLabel = new JLabel("Test");
        portLabel.setBounds(260, 275, 250, 50);
        portLabel.setForeground(Color.WHITE);
        portLabel.setFont(new Font("Open Sans", Font.BOLD, 30));

        backbgroundImage.add(ipLabel);
        backbgroundImage.add(portLabel);


        container.add(mainPane, "Main");
        container.add(settingPane, "Settings");
        setAlwaysOnTop(true);


        startServer();
        minimizeToTray();
        trayIcon.displayMessage("Server Is Running", "", TrayIcon.MessageType.INFO);


    }

    public void startServer() {
        LOGGER.info("Start");

        try {
            service = new CFService(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
        service.start();


    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(minimizeButtonMain) || e.getSource().equals(minimizeButtonSetting)) {
            minimizeToTray();
            trayIcon.displayMessage("Server Is Minimized", "right click here if you want to close the server", TrayIcon.MessageType.INFO);
        }

        if (e.getSource().equals(settingButton) || e.getSource().equals(backButton)) {
            cardLayout.next(container);


        }

        if (e.getSource().equals(showApp)) {
            if (trayIcon != null)
                tray.remove(trayIcon);
            checkScreenResolution();
            setVisible(true);
        }

        if (e.getSource().equals(closeApp)) {
            closeProgram();
        }


    }


    public void checkScreenResolution() {
        int currentSizeNeeded = Math.min((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2;

        if (frameSize != currentSizeNeeded) {
            frameSize = currentSizeNeeded;
            buttonSize = (int) (frameSize * 0.08);

            setSize(frameSize, frameSize);

            Image bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/MinimizeButton.png"))
                    .getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
            minimizeButtonMain.setIcon(new ImageIcon(bimg));
            minimizeButtonMain.setBounds(frameSize - buttonSize - 15, 10, buttonSize, buttonSize);

            bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/MinimizeButton.png"))
                    .getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
            minimizeButtonSetting.setIcon(new ImageIcon(bimg));
            minimizeButtonSetting.setBounds(frameSize - buttonSize - 15, 10, buttonSize, buttonSize);

            bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/SettingButton.png"))
                    .getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
            settingButton.setIcon(new ImageIcon(bimg));
            settingButton.setBounds(15, 10, buttonSize, buttonSize);

            bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/MainBackground.png"))
                    .getImage().getScaledInstance(frameSize, frameSize, Image.SCALE_SMOOTH);
            backbgroundImage.setIcon(new ImageIcon(bimg));
            backbgroundImage.setBounds(0, 0, frameSize, frameSize);

            repaint();


        }


    }

    public void setIpAndPort() {
        ipLabel.setText(service.getExternalIp());
        portLabel.setText(service.getPort());
    }


    public void minimizeToTray() {
        //Check the SystemTray is supported
        if (popup == null) {
            if (!SystemTray.isSupported()) {
                LOGGER.warning("SystemTray is not supported");
                return;
            }
        }

        popup = new PopupMenu();
        trayIcon = new TrayIcon(new ImageIcon(MainFrame.class.getResource("/NewServerDesign/TrayIcon.png")).getImage());

        // Create a pop-up menu components

        Menu displayMenu = new Menu("Display");
        showApp = new MenuItem("Show App");
        showApp.addActionListener(this);
        aboutItem = new MenuItem("About");
        aboutItem.addActionListener(this);
        closeApp = new MenuItem("Exit");
        closeApp.addActionListener(this);

        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(displayMenu);
        displayMenu.add(showApp);
        popup.add(closeApp);

        trayIcon.setPopupMenu(popup);

        trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    checkScreenResolution();

                    setVisible(true);
                    tray.remove(trayIcon);
                }
            }

        });

        try {

            tray.add(trayIcon);
        } catch (AWTException e1) {
            LOGGER.warning("TrayIcon could not be added.");
        }
        setVisible(false);

    }

    public void closeProgram() {
        running = false;
        System.exit(0);

        closeService();

    }


    public void closeService() {
        if (service != null)
            try {
                service.close();
            } catch (IOException e) {

            }
    }


}
