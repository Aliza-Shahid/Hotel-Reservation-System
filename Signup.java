import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Signup extends JFrame {

    private JLabel l1, l2, l3, l4, l5, l6, l7, l8;
    private JTextField u, a, pn;
    private JPasswordField p, cp;
    private JComboBox<String> q; 
    private JButton b1;

    public Signup() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("Signup");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(false);
        setSize(400, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(230, 230, 250));

        l1 = new JLabel("Name");
        l1.setBounds(30, 50, 500, 50);

        l2 = new JLabel("Phone Number");
        l2.setBounds(30, 115, 500, 50);

        l3 = new JLabel("Create Account");
        l3.setBounds(30, 20, 500, 50);
        l3.setFont(new Font("Arial", Font.BOLD, 23));

        l4 = new JLabel("Password");
        l4.setBounds(30, 185, 500, 50);

        l5 = new JLabel("< Back");
        l5.setBounds(10, 0, 95, 30);
        l5.setCursor(new Cursor(Cursor.HAND_CURSOR));
        l5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to go back?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    new Login();
                }
            }
        });

        l6 = new JLabel("Confirm Password");
        l6.setBounds(30, 255, 500, 50);

        l7 = new JLabel("Security Question");
        l7.setBounds(30, 325, 500, 50);

        l8 = new JLabel("Answer");
        l8.setBounds(30, 395, 500, 50);

        add(l1);
        add(l2);
        add(l3);
        add(l4);
        add(l5);
        add(l6);
        add(l7);
        add(l8);

        u = new JTextField();
        u.setBounds(30, 90, 280, 30);
        u.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    pn.requestFocus();
                }
            }
        });
        pn = new JTextField();
        pn.setBounds(30, 155, 280, 30);
        pn.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    p.requestFocus();
                }
            }
        });
        p = new JPasswordField();
        p.setBounds(30, 225, 280, 30);
        p.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cp.requestFocus();
                }
            }
        });
        cp = new JPasswordField();
        cp.setBounds(30, 295, 280, 30);
        
        String[] questions = {
            "Select a security question?",
            "What is your pet's name?",
            "Who is your favorite personality?",
            "What was the name of your first school?",
            "What is your favorite book?",
            "What is your dream country to visit?",
        };
        q = new JComboBox<>(questions);
        q.setBounds(30, 365, 280, 30);
        
        a = new JTextField();
        a.setBounds(30, 435, 280, 30);

        add(u);
        add(p);
        add(cp);
        add(pn);
        add(q);
        add(a);

        b1 = new JButton("Signup");
        b1.setBounds(120, 500, 95, 30);
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = u.getText().trim();
                String phoneNumber = pn.getText().trim();
                String password = String.valueOf(p.getPassword());
                String confirmPassword = String.valueOf(cp.getPassword());
                String answer = a.getText().trim();
                if (name.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || answer.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String validationResult = isValidPassword(password, confirmPassword);
                String phoneValidationResult = isValidPhoneNumber(phoneNumber);
        
                if ("success".equals(validationResult) && "success".equals(phoneValidationResult)) {
                    saveUser(new User(name, phoneNumber, password, q.getSelectedItem().toString(), answer));
                    dispose();
                    new Login();
                } else {
                    String errorMessage = validationResult.equals("success") ? phoneValidationResult : validationResult;
                    JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
               
        add(b1);
        setLayout(null);
        setVisible(true);
    }

    public void saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt", true))) {
            writer.write(user.toString()); // Assuming User class overrides toString() method to format user data
            writer.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromString(line); // Assuming User class has a fromString() method to parse user data
                users.add(user);
            }
        } catch (FileNotFoundException e) {
            System.out.println("User file not found, returning empty list");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean hasSpecialCharacter(String password) {
        for (char ch : password.toCharArray()) {
            if (!Character.isLetterOrDigit(ch)) {
                return true;
            }
        }
        return false;
    }

    public String isValidPassword(String password, String confirmPassword) {
        boolean Flag;
        if (hasSpecialCharacter(password)) {
            Flag = true;
        } else {
            Flag = false;
        }
    
        if (!password.equals(confirmPassword) && !Flag && password.length() < 8)
            return "Passwords do not match, and password should be at least 8 characters long with at least one special character.";
        else if (!password.equals(confirmPassword) && !Flag && password.length() >= 8)
            return "Passwords do not match, and password should contain at least one special character.";
        else if (!password.equals(confirmPassword) && password.length() < 8 && Flag)
            return "Passwords do not match, and password should be at least 8 characters long.";
        else if (password.equals(confirmPassword) && password.length() < 8 && !Flag)
            return "Password should be at least 8 characters long with at least one special character.";
        else if (password.equals(confirmPassword) && password.length() >= 8 && !Flag)
            return "Password should contain at least one special character.";
        else if (password.equals(confirmPassword) && password.length() < 8 && Flag)
            return "Password should be at least 8 characters long.";
        else if (!password.equals(confirmPassword) && password.length() >= 8 && Flag)
            return "Password and confirm password do not match.";

        return "success";
    }

    public String isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber.matches("0\\d{10}")) {
            return "success";
        } else {
            return "Phone number should start with 0, be 11 digits long, and contain only digits.";
        }
    }
}
