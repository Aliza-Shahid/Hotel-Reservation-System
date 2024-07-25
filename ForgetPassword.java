import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class ForgetPassword extends JFrame {
    private JLabel l1, l3;
    private JTextField u, a;
    private JComboBox<String> securityQuestionComboBox;
    private JButton resetBtn;

    String[] securityQuestions = {
        "Select a security question?",
        "What is your pet's name?",
        "Who is your favorite personality?",
        "What was the name of your first school?",
        "What is your favorite book?",
        "What is your dream country to visit?",
    };

    public ForgetPassword() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("Forget Password");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(false);
        setSize(400, 400);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(230, 230, 250));

        l1 = new JLabel("Enter your Phone number");
        l1.setBounds(30, 30, 500, 30);

        l3 = new JLabel("Answer to Security Question");
        l3.setBounds(30, 150, 500, 30);

        u = new JTextField();
        u.setBounds(30, 60, 280, 30);

        securityQuestionComboBox = new JComboBox<>(securityQuestions);
        securityQuestionComboBox.setBounds(30, 115, 280, 30);

        a = new JTextField();
        a.setBounds(30, 180, 280, 30);

        resetBtn = new JButton("Reset Password");
        resetBtn.setBounds(120, 240, 150, 30);
        resetBtn.setForeground(Color.RED);

        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phoneNumber = u.getText().trim();
                String selectedQuestion = (String) securityQuestionComboBox.getSelectedItem();
                String answer = a.getText().trim();

                if (phoneNumber.isEmpty() || selectedQuestion.equals(securityQuestions[0]) || answer.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String storedAnswer = getSecurityAnswer(phoneNumber, selectedQuestion);
                if (storedAnswer != null && storedAnswer.trim().equals(answer)) {
                    String newPassword = promptForNewPassword();
                    if (newPassword != null && !newPassword.isEmpty()) {
                        resetPassword(phoneNumber, newPassword);
                        JOptionPane.showMessageDialog(null, "Password reset successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new Login(); // Open the login class after successful password reset
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect phone number, question, or answer", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(l1);
        add(u);
        add(securityQuestionComboBox);
        add(l3);
        add(a);
        add(resetBtn);

        setLayout(null);
        setVisible(true);
    }

    private String getSecurityAnswer(String phoneNumber, String selectedQuestion) {
        File file = new File("data.txt");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].trim().equals(phoneNumber) && parts[3].trim().equals(selectedQuestion)) {
                    return parts[4].trim(); 
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private String promptForNewPassword() {
        return JOptionPane.showInputDialog(null, "Enter new password", "Reset Password", JOptionPane.PLAIN_MESSAGE);
    }

    private void resetPassword(String phoneNumber, String newPassword) {
        File file = new File("data.txt");
        File tempFile = new File("temp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[1].trim().equals(phoneNumber)) {
                    parts[2] = newPassword;
                    parts[2] = parts[2].trim();
                    line = String.join(",", parts);
                }
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error resetting password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        if (!file.delete() || !tempFile.renameTo(file)) {
            JOptionPane.showMessageDialog(null, "Error resetting password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
