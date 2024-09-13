package chatting.application;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Chatting implements ActionListener {
    private final JTextField text;
    private final JPanel a1;
    private static final Box vertical = Box.createVerticalBox();
    private static final JFrame f = new JFrame();
    private static DataOutputStream dout;
    private JScrollPane scrollPane; // Reference to the JScrollPane

    public Chatting() {
        f.setLayout(null);

        JPanel p1 = new JPanel();
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 450, 70);
        p1.setLayout(null);
        f.add(p1);

        JLabel dp = new JLabel(loadIcon("icons/dp.png", 50, 50));
        dp.setBounds(5, 10, 50, 50);
        p1.add(dp);

        JLabel video = new JLabel(loadIcon("icons/video.png", 40, 40));
        video.setBounds(300, 20, 30, 30);
        p1.add(video);

        JLabel call = new JLabel(loadIcon("icons/call.png", 30, 30));
        call.setBounds(360, 20, 30, 30);
        p1.add(call);

        JLabel dots = new JLabel(loadIcon("icons/dots.png", 10, 25));
        dots.setBounds(420, 20, 10, 25);
        p1.add(dots);

        JLabel name = new JLabel("Hari");
        name.setBounds(70, 15, 100, 18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN SERIF", Font.BOLD, 18));
        p1.add(name);

        JLabel status = new JLabel("Active Now");
        status.setBounds(70, 35, 100, 18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN SERIF", Font.PLAIN, 14));
        p1.add(status);

        a1 = new JPanel();
        a1.setBounds(5, 75, 425, 570);
        a1.setLayout(new BorderLayout());
        f.add(a1);

        scrollPane = new JScrollPane(a1);
        scrollPane.setBounds(5, 75, 425, 570);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        f.add(scrollPane);

        text = new JTextField();
        text.setBounds(5, 655, 310, 40);
        text.setFont(new Font("SAN SERIF", Font.PLAIN, 16));
        f.add(text);

        JButton send = new JButton("Send");
        send.setBounds(320, 655, 123, 40);
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.addActionListener(this);
        send.setFont(new Font("SAN SERIF", Font.PLAIN, 16));
        f.add(send);

        f.setSize(450, 700);
        f.setLocation(200, 50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);
        f.setVisible(true);

        setupServer();
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(path));
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return new ImageIcon(image);
    }

    private void setupServer() {
        new Thread(() -> {
            try {
                ServerSocket skt = new ServerSocket(60001);
                while (true) {
                    Socket s = skt.accept();
                    DataInputStream din = new DataInputStream(s.getInputStream());
                    dout = new DataOutputStream(s.getOutputStream());

                    while (true) {
                        String msg = din.readUTF();
                        JPanel panel = formatLabel(msg);

                        JPanel left = new JPanel(new BorderLayout());
                        left.add(panel, BorderLayout.LINE_START);

                        SwingUtilities.invokeLater(() -> {
                            vertical.add(left);
                            vertical.add(Box.createVerticalStrut(15));
                            a1.add(vertical, BorderLayout.PAGE_START);
                            scrollToBottom(); // Scroll to the bottom
                            f.validate();
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            String out = text.getText();
            JPanel p2 = formatLabel(out);

            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);

            SwingUtilities.invokeLater(() -> {
                vertical.add(right);
                vertical.add(Box.createVerticalStrut(15));
                a1.add(vertical, BorderLayout.PAGE_START);

                f.repaint();
                f.invalidate();
                f.validate();
                scrollToBottom(); // Scroll to the bottom
            });

            dout.writeUTF(out);
            text.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scrollToBottom() {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        SwingUtilities.invokeLater(() -> verticalBar.setValue(verticalBar.getMaximum()));
    }

    private JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tohoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));
        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel(sdf.format(cal.getTime()));
        panel.add(time);

        return panel;
    }

    public static void main(String[] args) {
        new Login("server");
    }
}
