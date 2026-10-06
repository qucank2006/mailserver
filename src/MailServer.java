import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MailServer {

    private static final int PORT = 2345;
    private static final String MAIL_FOLDER = "mail_data";

    public static void main(String[] args) {

        try {
            Files.createDirectories(Paths.get(MAIL_FOLDER));

            DatagramSocket serverSocket =
                    new DatagramSocket(PORT);

            System.out.println(
                    "Mail Server đang chạy tại cổng " + PORT
            );

            while (true) {

                byte[] receiveData = new byte[4096];

                DatagramPacket receivePacket =
                        new DatagramPacket(
                                receiveData,
                                receiveData.length
                        );

                serverSocket.receive(receivePacket);

                String request = new String(
                        receivePacket.getData(),
                        0,
                        receivePacket.getLength()
                );

                String response =
                        handleRequest(request);

                byte[] sendData =
                        response.getBytes();

                InetAddress clientAddress =
                        receivePacket.getAddress();

                int clientPort =
                        receivePacket.getPort();

                DatagramPacket sendPacket =
                        new DatagramPacket(
                                sendData,
                                sendData.length,
                                clientAddress,
                                clientPort
                        );

                serverSocket.send(sendPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // XỬ LÝ REQUEST
    // =====================================================

    private static String handleRequest(String request) {

        String[] parts =
                request.split("\\|", 4);

        try {

            switch (parts[0]) {

                case "REGISTER":
                    return register(
                            parts[1],
                            parts[2]
                    );

                case "LOGIN":
                    return login(
                            parts[1],
                            parts[2]
                    );

                case "SEND":
                    return sendMail(
                            parts[1],
                            parts[2],
                            parts[3]
                    );

                case "READ":
                    return readMail(
                            parts[1],
                            parts[2]
                    );

                default:
                    return "Lệnh không hợp lệ";
            }

        } catch (Exception e) {
            return "Có lỗi xảy ra";
        }
    }

    // =====================================================
    // REGISTER
    // =====================================================

    private static String register(
            String username,
            String password
    ) throws IOException {

        Path userFolder =
                Paths.get(MAIL_FOLDER, username);

        if (Files.exists(userFolder)) {
            return "Tài khoản đã tồn tại";
        }

        Files.createDirectories(userFolder);

        // Lưu password
        Path accountFile =
                userFolder.resolve("account.txt");

        Files.writeString(
                accountFile,
                password
        );

        // Email chào mừng
        Path welcomeFile =
                userFolder.resolve("new_email.txt");

        String welcome =
                "From: Mail Server\n"
                        + "To: " + username + "\n\n"
                        + "Thank you for using this service. "
                        + "We hope that you will feel comfortable.";

        Files.writeString(
                welcomeFile,
                welcome
        );

        return "Đăng ký thành công";
    }

    // =====================================================
    // LOGIN
    // =====================================================

    private static String login(
            String username,
            String password
    ) throws IOException {

        Path userFolder =
                Paths.get(MAIL_FOLDER, username);

        if (!Files.exists(userFolder)) {
            return "Tài khoản không tồn tại";
        }

        Path accountFile =
                userFolder.resolve("account.txt");

        if (!Files.exists(accountFile)) {
            return "Tài khoản không hợp lệ";
        }

        String savedPassword =
                Files.readString(accountFile);

        if (!savedPassword.equals(password)) {
            return "Sai mật khẩu";
        }

        File[] files =
                userFolder.toFile().listFiles();

        StringBuilder result =
                new StringBuilder(
                        "Danh sách email:\n"
                );

        if (files != null) {

            for (File file : files) {

                if (file.isFile()
                        && !file.getName()
                        .equals("account.txt")) {

                    result.append(
                            file.getName()
                    ).append("\n");
                }
            }
        }

        return result.toString();
    }

    // =====================================================
    // SEND MAIL
    // =====================================================

    private static String sendMail(
            String sender,
            String receiver,
            String content
    ) throws IOException {

        Path receiverFolder =
                Paths.get(
                        MAIL_FOLDER,
                        receiver
                );

        if (!Files.exists(receiverFolder)) {
            return "Tài khoản người nhận không tồn tại";
        }

        String fileName =
                "email_"
                        + System.currentTimeMillis()
                        + ".txt";

        Path emailFile =
                receiverFolder.resolve(fileName);

        String emailContent =
                "From: " + sender + "\n"
                        + "To: " + receiver + "\n\n"
                        + content;

        Files.writeString(
                emailFile,
                emailContent
        );

        return "Gửi email thành công";
    }

    // =====================================================
    // READ MAIL
    // =====================================================

    private static String readMail(
            String username,
            String fileName
    ) throws IOException {

        Path emailFile =
                Paths.get(
                        MAIL_FOLDER,
                        username,
                        fileName
                );

        if (!Files.exists(emailFile)) {
            return "Email không tồn tại";
        }

        return Files.readString(emailFile);
    }
}