import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class MailClient extends JFrame {

    private DatagramSocket clientSocket;
    private InetAddress serverAddress;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Login
    private JTextField usernameField;
    private JPasswordField passwordField;

    // Mail
    private JLabel userLabel;
    private DefaultListModel<String> mailListModel;
    private JList<String> mailList;

    // Session
    private String currentUser;
    private String currentPassword;

    // Colors
    private final Color BACKGROUND = new Color(15, 18, 24);
    private final Color PANEL = new Color(23, 28, 37);
    private final Color INPUT = new Color(31, 38, 49);
    private final Color TEXT = new Color(230, 234, 240);
    private final Color MUTED = new Color(140, 150, 165);
    private final Color ACCENT = new Color(79, 140, 255);
    private final Color GREEN = new Color(52, 211, 153);

    public MailClient() {

        setTitle("Mail Client");
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectUDP();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createMailPanel(), "MAIL");

        add(mainPanel);

        cardLayout.show(mainPanel, "LOGIN");
    }

    // =====================================================
    // UDP
    // =====================================================

    private void connectUDP() {

        try {

            clientSocket = new DatagramSocket();

            serverAddress = InetAddress.getByName(
                    IPConfig.SERVER_IP
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tạo UDP Socket hoặc IP Server không hợp lệ"
            );
        }
    }

    private String sendRequest(String request) {

        try {

            byte[] sendData =
                    request.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket =
                    new DatagramPacket(
                            sendData,
                            sendData.length,
                            serverAddress,
                            IPConfig.SERVER_PORT
                    );

            clientSocket.send(sendPacket);

            byte[] receiveData =
                    new byte[4096];

            DatagramPacket receivePacket =
                    new DatagramPacket(
                            receiveData,
                            receiveData.length
                    );

            clientSocket.receive(receivePacket);

            return new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength(),
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {

            return "Không thể kết nối Server";
        }
    }

    // =====================================================
    // LOGIN PANEL
    // =====================================================

    private JPanel createLoginPanel() {

        JPanel background =
                new JPanel(new GridBagLayout());

        background.setBackground(BACKGROUND);

        JPanel box = new JPanel();

        box.setPreferredSize(
                new Dimension(430, 390)
        );

        box.setBackground(PANEL);

        box.setLayout(
                new BoxLayout(
                        box,
                        BoxLayout.Y_AXIS
                )
        );

        box.setBorder(
                new EmptyBorder(
                        35,
                        45,
                        35,
                        45
                )
        );

        JLabel smallTitle =
                new JLabel("UDP MAIL SERVICE");

        smallTitle.setForeground(ACCENT);

        smallTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel title =
                new JLabel("MAIL CLIENT");

        title.setForeground(TEXT);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        // =====================
        // USERNAME
        // =====================

        JLabel usernameLabel =
                new JLabel("USERNAME");

        usernameLabel.setForeground(MUTED);

        usernameLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        usernameField =
                new JTextField();

        styleTextField(usernameField);

        usernameField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        // =====================
        // PASSWORD
        // =====================

        JLabel passwordLabel =
                new JLabel("PASSWORD");

        passwordLabel.setForeground(MUTED);

        passwordLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        passwordField =
                new JPasswordField();

        styleTextField(passwordField);

        passwordField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        // =====================
        // BUTTON
        // =====================

        JButton loginButton =
                createButton(
                        "LOGIN",
                        ACCENT
                );

        JButton registerButton =
                createButton(
                        "CREATE ACCOUNT",
                        INPUT
                );

        loginButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        registerButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        loginButton.addActionListener(
                e -> login()
        );

        registerButton.addActionListener(
                e -> register()
        );

        // Enter password -> login
        passwordField.addActionListener(
                e -> login()
        );

        // =====================
        // ADD COMPONENT
        // =====================

        box.add(smallTitle);

        box.add(
                Box.createVerticalStrut(5)
        );

        box.add(title);

        box.add(
                Box.createVerticalStrut(30)
        );

        box.add(usernameLabel);

        box.add(
                Box.createVerticalStrut(5)
        );

        box.add(usernameField);

        box.add(
                Box.createVerticalStrut(15)
        );

        box.add(passwordLabel);

        box.add(
                Box.createVerticalStrut(5)
        );

        box.add(passwordField);

        box.add(
                Box.createVerticalStrut(20)
        );

        box.add(loginButton);

        box.add(
                Box.createVerticalStrut(10)
        );

        box.add(registerButton);

        background.add(box);

        return background;
    }

    // =====================================================
    // MAIL PANEL
    // =====================================================

    private JPanel createMailPanel() {

        JPanel panel =
                new JPanel(new BorderLayout());

        panel.setBackground(BACKGROUND);

        panel.add(
                createSidebar(),
                BorderLayout.WEST
        );

        panel.add(
                createInboxPanel(),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =====================================================
    // SIDEBAR
    // =====================================================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(
                new Dimension(210, 0)
        );

        sidebar.setBackground(PANEL);

        sidebar.setLayout(
                new BoxLayout(
                        sidebar,
                        BoxLayout.Y_AXIS
                )
        );

        sidebar.setBorder(
                new EmptyBorder(
                        30,
                        20,
                        30,
                        20
                )
        );

        JLabel logo =
                new JLabel("MAIL CLIENT");

        logo.setForeground(TEXT);

        logo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        userLabel =
                new JLabel("User");

        userLabel.setForeground(GREEN);

        JButton inboxButton =
                createButton(
                        "INBOX",
                        INPUT
                );

        JButton composeButton =
                createButton(
                        "COMPOSE",
                        INPUT
                );

        JButton logoutButton =
                createButton(
                        "LOGOUT",
                        INPUT
                );

        inboxButton.addActionListener(
                e -> refreshInbox()
        );

        composeButton.addActionListener(
                e -> showCompose()
        );

        logoutButton.addActionListener(
                e -> logout()
        );

        sidebar.add(logo);

        sidebar.add(
                Box.createVerticalStrut(10)
        );

        sidebar.add(userLabel);

        sidebar.add(
                Box.createVerticalStrut(40)
        );

        sidebar.add(inboxButton);

        sidebar.add(
                Box.createVerticalStrut(10)
        );

        sidebar.add(composeButton);

        sidebar.add(
                Box.createVerticalGlue()
        );

        sidebar.add(logoutButton);

        return sidebar;
    }

    // =====================================================
    // INBOX
    // =====================================================

    private JPanel createInboxPanel() {

        JPanel inbox =
                new JPanel(
                        new BorderLayout(
                                10,
                                15
                        )
                );

        inbox.setBackground(BACKGROUND);

        inbox.setBorder(
                new EmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setBackground(BACKGROUND);

        JLabel title =
                new JLabel("INBOX");

        title.setForeground(TEXT);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        25
                )
        );

        JButton refreshButton =
                createButton(
                        "REFRESH",
                        INPUT
                );

        refreshButton.addActionListener(
                e -> refreshInbox()
        );

        header.add(
                title,
                BorderLayout.WEST
        );

        header.add(
                refreshButton,
                BorderLayout.EAST
        );

        mailListModel =
                new DefaultListModel<>();

        mailList =
                new JList<>(mailListModel);

        mailList.setBackground(PANEL);
        mailList.setForeground(TEXT);

        mailList.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );

        mailList.setFixedCellHeight(40);

        mailList.setSelectionBackground(
                new Color(45, 55, 70)
        );

        mailList.setSelectionForeground(TEXT);

        mailList.setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        // Click email -> đọc mail
        mailList.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                String selected =
                        mailList.getSelectedValue();

                if (selected != null) {

                    readMail(
                            selected.trim()
                    );
                }
            }
        });

        JScrollPane scrollPane =
                new JScrollPane(mailList);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        INPUT
                )
        );

        inbox.add(
                header,
                BorderLayout.NORTH
        );

        inbox.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return inbox;
    }

    // =====================================================
    // REGISTER
    // =====================================================

    private void register() {

        String username =
                usernameField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField
                                .getPassword()
                );

        if (username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập username và password"
            );

            return;
        }

        String response =
                sendRequest(
                        "REGISTER|"
                                + username
                                + "|"
                                + password
                );

        JOptionPane.showMessageDialog(
                this,
                response
        );
    }

    // =====================================================
    // LOGIN
    // =====================================================

    private void login() {

        String username =
                usernameField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField
                                .getPassword()
                );

        if (username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập username và password"
            );

            return;
        }

        String response =
                sendRequest(
                        "LOGIN|"
                                + username
                                + "|"
                                + password
                );

        if (response.startsWith(
                "Danh sách email:"
        )) {

            currentUser = username;
            currentPassword = password;

            userLabel.setText(
                    "● " + currentUser
            );

            updateMailList(response);

            cardLayout.show(
                    mainPanel,
                    "MAIL"
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    response
            );
        }
    }

    // =====================================================
    // REFRESH INBOX
    // =====================================================

    private void refreshInbox() {

        if (currentUser == null) {
            return;
        }

        String response =
                sendRequest(
                        "LOGIN|"
                                + currentUser
                                + "|"
                                + currentPassword
                );

        if (response.startsWith(
                "Danh sách email:"
        )) {

            updateMailList(response);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    response
            );
        }
    }

    private void updateMailList(
            String response
    ) {

        mailListModel.clear();

        if (!response.startsWith(
                "Danh sách email:"
        )) {
            return;
        }

        String[] lines =
                response.split("\n");

        for (
                int i = 1;
                i < lines.length;
                i++
        ) {

            if (!lines[i].isBlank()) {

                mailListModel.addElement(
                        lines[i].trim()
                );
            }
        }
    }

    // =====================================================
    // COMPOSE
    // =====================================================

    private void showCompose() {

        JDialog dialog =
                new JDialog(
                        this,
                        "New Message",
                        true
                );

        dialog.setSize(
                550,
                450
        );

        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();

        panel.setBackground(BACKGROUND);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBorder(
                new EmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        JLabel title =
                new JLabel(
                        "NEW MESSAGE"
                );

        title.setForeground(TEXT);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        JLabel fromLabel =
                new JLabel(
                        "FROM: "
                                + currentUser
                );

        fromLabel.setForeground(GREEN);

        JLabel toLabel =
                new JLabel("TO");

        toLabel.setForeground(MUTED);

        JTextField receiverField =
                new JTextField();

        styleTextField(receiverField);

        receiverField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );

        JLabel messageLabel =
                new JLabel("MESSAGE");

        messageLabel.setForeground(MUTED);

        JTextArea contentArea =
                new JTextArea();

        contentArea.setBackground(INPUT);
        contentArea.setForeground(TEXT);
        contentArea.setCaretColor(TEXT);

        contentArea.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JScrollPane scroll =
                new JScrollPane(
                        contentArea
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        INPUT
                )
        );

        JButton sendButton =
                createButton(
                        "SEND",
                        ACCENT
                );

        sendButton.addActionListener(e -> {

            String receiver =
                    receiverField
                            .getText()
                            .trim();

            String content =
                    contentArea
                            .getText()
                            .trim();

            sendMail(
                    receiver,
                    content,
                    dialog
            );
        });

        panel.add(title);

        panel.add(
                Box.createVerticalStrut(8)
        );

        panel.add(fromLabel);

        panel.add(
                Box.createVerticalStrut(20)
        );

        panel.add(toLabel);

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(receiverField);

        panel.add(
                Box.createVerticalStrut(15)
        );

        panel.add(messageLabel);

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(scroll);

        panel.add(
                Box.createVerticalStrut(15)
        );

        sendButton.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );

        panel.add(sendButton);

        dialog.add(panel);

        dialog.setVisible(true);
    }

    // =====================================================
    // SEND MAIL
    // =====================================================

    private void sendMail(
            String receiver,
            String content,
            JDialog dialog
    ) {

        if (receiver.isEmpty()
                || content.isEmpty()) {

            JOptionPane.showMessageDialog(
                    dialog,
                    "Vui lòng nhập người nhận và nội dung"
            );

            return;
        }

        String response =
                sendRequest(
                        "SEND|"
                                + currentUser
                                + "|"
                                + receiver
                                + "|"
                                + content
                );

        JOptionPane.showMessageDialog(
                dialog,
                response
        );

        if (response.equals(
                "Gửi email thành công"
        )) {

            dialog.dispose();
        }
    }

    // =====================================================
    // READ MAIL
    // =====================================================

    private void readMail(
            String fileName
    ) {

        String response =
                sendRequest(
                        "READ|"
                                + currentUser
                                + "|"
                                + fileName
                );

        JDialog dialog =
                new JDialog(
                        this,
                        "Read Email",
                        true
                );

        dialog.setSize(
                570,
                420
        );

        dialog.setLocationRelativeTo(this);

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                10,
                                15
                        )
                );

        panel.setBackground(BACKGROUND);

        panel.setBorder(
                new EmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        JPanel header =
                new JPanel();

        header.setLayout(
                new BoxLayout(
                        header,
                        BoxLayout.Y_AXIS
                )
        );

        header.setBackground(BACKGROUND);

        JLabel title =
                new JLabel("MESSAGE");

        title.setForeground(TEXT);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        JLabel fileLabel =
                new JLabel(fileName);

        fileLabel.setForeground(MUTED);

        header.add(title);

        header.add(
                Box.createVerticalStrut(5)
        );

        header.add(fileLabel);

        JTextArea messageArea =
                new JTextArea(response);

        messageArea.setEditable(false);

        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        messageArea.setBackground(PANEL);
        messageArea.setForeground(TEXT);

        messageArea.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        messageArea.setBorder(
                new EmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        messageArea
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        INPUT
                )
        );

        JButton closeButton =
                createButton(
                        "CLOSE",
                        INPUT
                );

        closeButton.addActionListener(
                e -> dialog.dispose()
        );

        JPanel bottom =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        bottom.setBackground(BACKGROUND);

        bottom.add(closeButton);

        panel.add(
                header,
                BorderLayout.NORTH
        );

        panel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        panel.add(
                bottom,
                BorderLayout.SOUTH
        );

        dialog.add(panel);

        dialog.setVisible(true);

        // Cho phép click lại cùng email
        mailList.clearSelection();
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    private void logout() {

        currentUser = null;
        currentPassword = null;

        usernameField.setText("");
        passwordField.setText("");

        mailListModel.clear();

        cardLayout.show(
                mainPanel,
                "LOGIN"
        );
    }

    // =====================================================
    // STYLE
    // =====================================================

    private JButton createButton(
            String text,
            Color color
    ) {

        JButton button =
                new JButton(text);

        button.setBackground(color);
        button.setForeground(TEXT);

        button.setFocusPainted(false);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );

        button.setPreferredSize(
                new Dimension(
                        140,
                        40
                )
        );

        return button;
    }

    private void styleTextField(
            JTextField field
    ) {

        field.setBackground(INPUT);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);

        field.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                INPUT
                        ),
                        new EmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MailClient client =
                    new MailClient();

            client.setVisible(true);
        });
    }
}