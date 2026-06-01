import javax.swing.*;

public class LoginUI extends JFrame {

    private final AuthService authService = new AuthService();

    JTextField     user;
    JPasswordField pass;

    public LoginUI() {
        setTitle("Login");
        setSize(300, 200);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel u = new JLabel("Username:");
        u.setBounds(30, 30, 80, 25);
        add(u);

        user = new JTextField();
        user.setBounds(120, 30, 120, 25);
        add(user);

        JLabel p = new JLabel("Password:");
        p.setBounds(30, 70, 80, 25);
        add(p);

        pass = new JPasswordField();
        pass.setBounds(120, 70, 120, 25);
        add(pass);

        JButton login = new JButton("Login");
        login.setBounds(90, 110, 100, 30);
        add(login);

        login.addActionListener(e -> {
            String enteredUser = user.getText().trim();
            String enteredPass = new String(pass.getPassword());

            if (enteredUser.isEmpty() || enteredPass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty.");
                return;
            }

            if (authService.checkCredentials(enteredUser, enteredPass)) {
                new HospitalUI();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password.");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
