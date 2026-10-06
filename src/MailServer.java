import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MailServer {

    private static final int PORT = 2345;
    private static final String MAIL_FOLDER = "mail_data";

    public static void main(String[] args) {

        try {
            Files.createDirectories(
                    Paths.get(MAIL_FOLDER)
            );

            DatagramSocket serverSocket =
                    new DatagramSocket(PORT);

            System.out.println(
                    "Mail Server đang chạy tại cổng " + PORT
            );

            while (true) {

                byte[] receiveData =
                        new byte[4096];

                DatagramPacket receivePacket =
                        new DatagramPacket(
                                receiveData,
                                receiveData.length
                        );

                serverSocket.receive(receivePacket);

                String request =
                        new String(
                                receivePacket.getData(),
                                0,
                                receivePacket.getLength(),
                                StandardCharsets.UTF_8
                        );

                // Lấy IP thật của Client gửi UDP
                String clientIP =
                        receivePacket
                                .getAddress()
                                .getHostAddress();

                String response =
                        handleRequest(
                                request,
                                clientIP
                        );

                byte[] sendData =
                        response.getBytes(
                                StandardCharsets.UTF_8
                        );

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
    // HANDLE REQUEST
    // =====================================================

    private static String handleRequest(
            String request,
            String clientIP
    ) {

        String[] parts =
                request.split("\\|", 4);

        try {

            switch (parts[0]) {

                case "REGISTER":

                    if (parts.length < 3) {
                        return "Dữ liệu đăng ký không hợp lệ";
                    }

                    return register(
                            parts[1],
                            parts[2]
                    );

                case "LOGIN":

                    if (parts.length < 3) {
                        return "Dữ liệu đăng nhập không hợp lệ";
                    }

                    return login(
                            parts[1],
                            parts[2]
                    );

                case "SEND":

                    if (parts.length < 4) {
                        return "Dữ liệu email không hợp lệ";
                    }

                    return sendMail(
                            parts[1],
                            parts[2],
                            parts[3],
                            clientIP
                    );

                case "READ":

                    if (parts.length < 3) {
                        return "Dữ liệu đọc email không hợp lệ";
                    }

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
                Paths.get(
                        MAIL_FOLDER,
                        username
                );

        if (Files.exists(userFolder)) {
            return "Tài khoản đã tồn tại";
        }

        Files.createDirectories(
                userFolder
        );

        // Lưu password
        Path accountFile =
                userFolder.resolve(
                        "account.txt"
                );

        Files.writeString(
                accountFile,
                password,
                StandardCharsets.UTF_8
        );

        // Tạo mail chào mừng
        Path welcomeFile =
                userFolder.resolve(
                        "new_email.txt"
                );

        String currentTime =
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:mm:ss"
                        )
                );

        String welcomeContent =
                "From: Mail Server\n"
                        + "To: " + username + "\n"
                        + "Time: " + currentTime + "\n"
                        + "IP: Server\n"
                        + "\n"
                        + "Thank you for using this service. "
                        + "We hope that you will feel comfortable.";

        Files.writeString(
                welcomeFile,
                welcomeContent,
                StandardCharsets.UTF_8
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
                Paths.get(
                        MAIL_FOLDER,
                        username
                );

        if (!Files.exists(userFolder)) {
            return "Tài khoản không tồn tại";
        }

        Path accountFile =
                userFolder.resolve(
                        "account.txt"
                );

        if (!Files.exists(accountFile)) {
            return "Tài khoản không hợp lệ";
        }

        String savedPassword =
                Files.readString(
                        accountFile,
                        StandardCharsets.UTF_8
                ).trim();

        if (!savedPassword.equals(password)) {
            return "Sai mật khẩu";
        }

        File[] files =
                userFolder
                        .toFile()
                        .listFiles();

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
            String content,
            String senderIP
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
                receiverFolder.resolve(
                        fileName
                );

        String sendTime =
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:mm:ss"
                        )
                );

        String emailContent =
                "From: " + sender + "\n"
                        + "To: " + receiver + "\n"
                        + "Time: " + sendTime + "\n"
                        + "IP: " + senderIP + "\n"
                        + "\n"
                        + content;

        Files.writeString(
                emailFile,
                emailContent,
                StandardCharsets.UTF_8
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

        return Files.readString(
                emailFile,
                StandardCharsets.UTF_8
        );
    }
}