import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by yoni on 24/07/2015.
 */
public class MainFrame extends JFrame implements ActionListener {

    private final static long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public final static Color backColor = Color.decode("#889092");


    public static Font font;

    private int frameSize = Math.min((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2;

    private int buttonSize = (int) (frameSize * 0.08);

    private JPanel mainPane;
    private JPanel settingPane;
    private JPanel clientPanel;
    private Container container;
    private CardLayout cardLayout;

    private JButton minimizeButtonMain;
    private JButton minimizeButtonSetting;
    private JButton settingButton;
    private JButton backButton;

//    private JLabel settingImage;
//    private JLabel backgroundImage;


    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel ipCellLabel;
    private JLabel portCellLabel;
    private ArrayList<JLabel> connectedClinets;


    private SystemTray tray = SystemTray.getSystemTray();
    private Image trayIconeImg;
    private TrayIcon trayIcon;
    private PopupMenu popup;
    private MenuItem aboutItem;
    private MenuItem showApp;
    private MenuItem closeApp;

    private CFService service;
    private boolean running;


    public MainFrame() {

        loadFont();

        Dimension trayIconSize = tray.getTrayIconSize();

        trayIconeImg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/TrayIcon.png"))
                .getImage().getScaledInstance((int) trayIconSize.getWidth(), (int) trayIconSize.getHeight(), Image.SCALE_SMOOTH);

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
        LOGGER.info("frame size: " + frameSize + " button size: " + buttonSize);


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


//        backgroundImage = new JLabel("");
//        bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/SettingBackground.png"))
//                .getImage().getScaledInstance(frameSize, frameSize, Image.SCALE_SMOOTH);
//        backgroundImage.setIcon(new ImageIcon(bimg));
//        backgroundImage.setBounds(0, 0, frameSize, frameSize);

        ipCellLabel = new JLabel();
//        bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/IpCell.png"))
//                .getImage().getScaledInstance(frameSize / 2, (int) (buttonSize * 0.6), Image.SCALE_SMOOTH);
//        ipCellLabel.setIcon(new ImageIcon(bimg));
        ipCellLabel.setBounds(frameSize / 4, frameSize / 3, frameSize / 2, (int) (buttonSize * 0.6));
        ipCellLabel.setForeground(Color.WHITE);
        ipCellLabel.setFont(font);
        ipCellLabel.setHorizontalTextPosition(JLabel.CENTER);
        ipCellLabel.setVerticalTextPosition(JLabel.CENTER);


        portCellLabel = new JLabel("PORT:");
//        bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/PortCell.png"))
//                .getImage().getScaledInstance(frameSize / 2, (int) (buttonSize * 0.6), Image.SCALE_SMOOTH);
//        portCellLabel.setIcon(new ImageIcon(bimg));
        portCellLabel.setBounds(frameSize / 4, (frameSize / 3) + (int) (buttonSize * 0.6) + buttonSize, frameSize / 2, (int) (buttonSize * 0.6));
        portCellLabel.setForeground(Color.WHITE);
        portCellLabel.setFont(font);
        portCellLabel.setHorizontalTextPosition(JLabel.CENTER);
        portCellLabel.setVerticalTextPosition(JLabel.CENTER);

        ipLabel = new JLabel("IP:");
        ipLabel.setBounds(frameSize / 5, frameSize / 3, frameSize / 2, (int) (buttonSize * 0.6));
        ipLabel.setForeground(Color.WHITE);
        ipLabel.setFont(font);

        settingPane.setBackground(backColor);
        mainPane.setBackground(backColor);


        mainPane.add(ipCellLabel);
        mainPane.add(portCellLabel);
//        mainPane.add(backgroundImage);


//        settingImage = new JLabel("");


//        bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/SettingBackground.png"))
//                .getImage().getScaledInstance(frameSize, frameSize, Image.SCALE_SMOOTH);
//        settingImage.setIcon(new ImageIcon(bimg));
//        settingImage.setBounds(0, 0, frameSize, frameSize);
        // settingPane.add(settingImage);


        //backgroundImage.add(ipLabel);
        //  backgroundImage.add(portLabel);

        connectedClinets = new ArrayList<JLabel>();
        clientPanel = new JPanel(new GridLayout(frameSize / buttonSize, 0, 5, 15));
        clientPanel.setBounds(frameSize / 4, buttonSize + 30, frameSize / 2, frameSize - buttonSize - 30);
        clientPanel.setOpaque(false);

        JLabel test = new JLabel("Connected Clients:");

        test.setSize(frameSize / 3, buttonSize / 2);
        test.setForeground(Color.WHITE);
        test.setFont(font);


        clientPanel.add(test);


        // settingImage.add(clientPanel);
        settingPane.add(clientPanel);

        container.add(mainPane, "Main");
        container.add(settingPane, "Settings");
        setShape(new RoundRectangle2D.Double(0, 0, frameSize, frameSize, 40, 40));

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
            // backgroundImage.setIcon(new ImageIcon(bimg));
            //  backgroundImage.setBounds(0, 0, frameSize, frameSize);

            repaint();


        }


    }

    public void setIpAndPort() {
        ipCellLabel.setText("IP:        " + service.getMyIp());
        portCellLabel.setText("PORT:        " + service.getPort());
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

        trayIcon = new TrayIcon(trayIconeImg);

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
        LOGGER.severe("Closing Controly");
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

    public void addClientToLabel(CFClient cfc) {

        JLabel temp = new JLabel(cfc.getClientName());
        Image bimg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/Cell.png"))
                .getImage().getScaledInstance(frameSize / 3, buttonSize / 2, Image.SCALE_SMOOTH);
        temp.setIcon(new ImageIcon(bimg));
        temp.setHorizontalTextPosition(JLabel.CENTER);
        temp.setVerticalTextPosition(JLabel.CENTER);
        temp.setSize(frameSize / 3, (int) (buttonSize / 1.5));
        temp.setForeground(Color.WHITE);
        temp.setFont(font);
        connectedClinets.add(temp);
        clientPanel.add(temp);
        clientPanel.validate();
        clientPanel.repaint();


    }

    public void loadFont() {
        InputStream is = MainFrame.class.getResourceAsStream("Fonts/OpenSans-Regular.ttf");
        try {
            //create the font to use. Specify the size!
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(font);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
