import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class Login extends JFrame {
    private JLabel l1, l2, l3, l4,l5;
    private JTextField u;
    private JPasswordField p;
    private JButton b1, b2;

    public Login() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("SIGN IN");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(false);
        setSize(400, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(230, 230, 250));

        l1 = new JLabel("Enter your Phone number for Sign-in ");
        l1.setBounds(30, 80, 500, 50);

        l2 = new JLabel("Password");
        l2.setBounds(30, 150, 500, 50);

        l3 = new JLabel("Sign In");
        l3.setBounds(40, 30, 500, 50);
        l3.setFont(new Font("Arial", Font.BOLD, 28));

        l4 = new JLabel("Forgot password ?");
        l4.setBounds(120, 230, 500, 50);
        l4.setForeground(Color.BLUE);
        l4.setCursor(new Cursor(Cursor.HAND_CURSOR));

        l4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                ForgetPassword fp = new ForgetPassword();
                fp.setVisible(true);
            }
        });

        l5 = new JLabel("<html><span style='color:red;'>By Signing up you agreed to our Terms Conditions & Privacy Policy</span></html>");
        l5.setBounds(30, 390, 350, 80);
        add(l1);
        add(l2);
        add(l3);
        add(l4);
        add(l5);

        u = new JTextField();
        u.setBounds(30, 120, 280, 30);
        u.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    p.requestFocus();
                }
            }
        });

        p = new JPasswordField();
        p.setBounds(30, 190, 280, 30);

        add(u);
        add(p);

        b1 = new JButton("Sign up");
        b1.setBounds(120, 325, 95, 30);
        b1.setForeground(Color.RED); 
        b2 = new JButton("Sign in");
        b2.setBounds(120, 280, 95, 30);
        b2.setForeground(Color.RED); 

        add(b1);
        add(b2);

        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Signup().setVisible(true);
            }
        });

        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredUsername = u.getText().trim(); 
                String enteredPassword = String.valueOf(p.getPassword());
                String phoneValidationResult = isValidPhoneNumber(enteredUsername);

                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
                
                if (!"success".equals(phoneValidationResult)) {
                    JOptionPane.showMessageDialog(null, phoneValidationResult, "Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }

                boolean usernameFound = false;
                boolean passwordCorrect = false;
                String userName = null; 

                File file = new File("data.txt");
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(",");
                        if (parts.length >= 3 && parts[1].trim().equals(enteredUsername)) { 
                            usernameFound = true;
                            userName = parts[0].trim().toUpperCase(); 
                            if (parts[2].trim().equals(enteredPassword)) { 
                                passwordCorrect = true;
                                break;
                            }
                        }
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (!usernameFound) {
                    JOptionPane.showMessageDialog(null, "Account not found", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!passwordCorrect) {
                    JOptionPane.showMessageDialog(null, "Password incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    dispose();
                    try (FileWriter writer = new FileWriter("loggedIn.txt")) {
                        writer.write(userName + System.lineSeparator());
                    } catch (IOException ex) {
                        System.out.println("File not found");
                    }
                    new Booking(userName); 
                }
            }
        });

        setLayout(null);
        setVisible(true);
        
    }

    public String isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber.matches("0\\d{10}")) {
            return "success";
        } else {
            return "Phone number should start with 0, be 11 digits long, and contain only digits.";
        }
    }
}
