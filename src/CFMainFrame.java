import aurelienribon.tweenengine.TweenManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;


public class CFMainFrame extends JFrame implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JLabel statusLabel;
    private JButton startButton;
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
    private int portNum;
    private String ipNum;
    private ConnectionFrame info;
    private int x, y;
    public static TweenManager tweenManager = new TweenManager();
    private boolean running;
    private int taskbarheight;

    /**
     * Launch the application.
     */


    /**
     * Create the frame.
     */
    public CFMainFrame() {

        taskbarheight = Toolkit.getDefaultToolkit().getScreenSize().height - GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
        portNum = 0;
        setSize(200, 215);
        setTitle("Controly");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false); //frame jumps because of this command
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        //contentPane.setBackground(new Color(51, 204, 255));
        contentPane.setLayout(null);

        //	setContentPane(new JLabel(new ImageIcon(CFMainFrame.class.getResource("/projectx/Resources/AppBackground.png"))));

        setUndecorated(true);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        x = (int) rect.getMaxX() - 193;
        y = (int) rect.getMaxY() - getHeight() - taskbarheight;
        setLocation(x, y);


        startButton = new JButton("");
        startButton.setBorder(null);
        startButton.setIcon(new ImageIcon(CFMainFrame.class.getResource("/Resources/ConnectButton.png")));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setBounds(59, 66, 82, 83);
        startButton.addActionListener(this);
        contentPane.add(startButton);

        statusLabel = new JLabel("Server Stopped");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBounds(10, 161, 181, 28);
        contentPane.add(statusLabel);


        setContentPane(contentPane);

        minimizeButton = new JButton("");

        minimizeButton.setIcon(new ImageIcon(CFMainFrame.class.getResource("/Resources/MinimizeButton.png")));
        minimizeButton.setBounds(10, 10, 18, 5);
        minimizeButton.setOpaque(false);
        minimizeButton.setContentAreaFilled(false);
        minimizeButton.setBorderPainted(false);
        minimizeButton.addActionListener(this);

        contentPane.add(minimizeButton);
        //a
        exitButton = new JButton("");
        exitButton.setIcon(new ImageIcon(CFMainFrame.class.getResource("/Resources/x-BUTTON.png")));
        exitButton.setBounds(175, 9, 16, 16);
        exitButton.setOpaque(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.addActionListener(this);
        contentPane.add(exitButton);


        // for now no use for setting button
/*	    settingButton = new JButton("");
        settingButton.setIcon(new ImageIcon(CFMainFrame.class.getResource("/Resources/SettingButton.png")));
		settingButton.setBounds(150, 10, 16, 16);
		settingButton.setOpaque(false);
		settingButton.setContentAreaFilled(false);
		settingButton.setBorderPainted(false);
		settingButton.addActionListener(this);
		contentPane.add(settingButton);*/

        backbgroundImage = new JLabel("");
        backbgroundImage.setIcon(new ImageIcon(CFMainFrame.class.getResource("/Resources/AppBackground.png")));
        backbgroundImage.setBounds(0, 0, 197, 216);
        contentPane.add(backbgroundImage);
        running = true;
        info = new ConnectionFrame(x, y);
        info.start();

        setAlwaysOnTop(true);

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {
                if (info.isShown())
                    info.hideFrame();

            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                if (!info.isShown() && service != null)
                    info.showFrame();
            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });


        new Thread(new Runnable() {
            private long lastMillis = -1;

            @Override
            public void run() {

                startServer();
                minimizeToTray();
                trayIcon.displayMessage("Server Is Running", "open the app to connect", TrayIcon.MessageType.INFO);

                while (running) {

                    if (lastMillis > 0) {
                        long currentMillis = System.currentTimeMillis();
                        final float delta = (currentMillis - lastMillis) / 1000f;
                        tweenManager.update(delta);

                        lastMillis = currentMillis;
                    } else {
                        lastMillis = System.currentTimeMillis();
                    }

                    try {
                        Thread.sleep(1000 / 60);

                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void actionPerformed(ActionEvent e) {
        //decide which button was pressed.


        if (e.getSource().equals(startButton)) {
            startServer();
        }

        if (e.getSource().equals(minimizeButton)) {

            if (service != null) {
                synchronized (info) {
                    info.notifyAll();
                }
            }
            minimizeToTray();
        }


        if (e.getSource().equals(settingButton)) {

            CFPopup.incoming(ipNum, portNum, tweenManager);
            System.out.println("WTF OMFG");
        }

        if (e.getSource().equals(showApp)) {
            if (trayIcon != null)
                tray.remove(trayIcon);
            if (service != null) {
                synchronized (info) {
                    info.notifyAll();
                }
            }
            setVisible(true);
        }
        if (e.getSource().equals(closeApp) || e.getSource().equals(exitButton)) {
            closeProgram();

        }
    }

    public void startServer() {
        CFTools.log("Start");
        if (service == null) { // run server if not running
            try {

                service = new CFService(tweenManager);
                service.start();
                ipNum = service.getIP();
                portNum = service.getPort();
                synchronized (info) {
                    info.setIpPort(ipNum, portNum);
                    info.notifyAll();

                }


            } catch (IOException e1) {
                CFTools.log("Couldn't start serversocket(0)");
                e1.printStackTrace();
            }
            statusLabel.setText("Server Started");
            //we should register a server.
        } else { // if pressed again stop service and defualt the vars
            statusLabel.setText("Server Stopped");
            closeService();
            ipNum = "Start Server First";
            portNum = 0;
            service = null;
            CFTools.log("Stop");

            synchronized (info) {
                info.notifyAll();
            }
        }
    }


    public void minimizeToTray() {
        //Check the SystemTray is supported
        if (popup == null) {
            if (!SystemTray.isSupported()) {
                System.out.println("SystemTray is not supported");
                return;
            }
            popup = new PopupMenu();
            trayIcon = new TrayIcon(new ImageIcon(CFMainFrame.class.getResource("/Resources/TrayIcon.png")).getImage());


            // Create a pop-up menu components
            MenuItem aboutItem = new MenuItem("About");
            Menu displayMenu = new Menu("Display");
            showApp = new MenuItem("Show App");

            showApp.addActionListener(this);
            closeApp = new MenuItem("Exit");
            closeApp.addActionListener(this);

            //Add components to pop-up menu
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
                        if (service != null) {
                            synchronized (info) {
                                info.notifyAll();
                            }
                        }
                        setVisible(true);
                        tray.remove(trayIcon);
                    }
                }

            });


        }

        try {

            tray.add(trayIcon);
        } catch (AWTException e1) {
            System.out.println("TrayIcon could not be added.");
        }
        setVisible(false);
    }

    public void closeProgram() {
        System.exit(0);
        running = false;
        closeService();
        if (info != null)
            info.closeThread();

    }

    public void closeService() {
        if (service != null)
            try {
                service.close();
            } catch (IOException e) {

            }
    }


}

