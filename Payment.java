import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.util.Date;

public class Payment extends JFrame {
    private JLabel bank, acc_num, cnic, back, text;
    private JComboBox<String> jc;
    private JTextField acc_field;
    private JButton pay;
    private String selected_item, cnic_num;
    private JFormattedTextField cnicField;
    private double totalPrice;
    private Date checkInDate;
    private Date checkOutDate;
    private String location;
    private String username;

    public Payment(double totalPrice, Date checkInDate, Date checkOutDate, String location,String username) {
        this.totalPrice = totalPrice;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.location = location;
        this.username = username;

        setSize(400, 600);
        setTitle("Payment");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        back = new JLabel("< Back");
        back.setBounds(10, 4, 95, 30);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to go back?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    // Return to the previous screen
                    new AvailableRooms(checkInDate, checkOutDate, location ,username);
                }
            }
        });

        bank = new JLabel("BANK");
        bank.setBounds(35, 70, 200, 30);

        String[] banks = {"Please Select Bank", "Allied Bank", "Faysal Bank", "Habib Bank Limited", "Mezan Bank Limited"};
        jc = new JComboBox<>(banks);
        jc.setBounds(35, 100, 200, 30);
        jc.setFocusable(false);

        acc_num = new JLabel("ACCOUNT NUMBER");
        acc_num.setBounds(35, 150, 200, 30);

        acc_field = new JTextField();
        acc_field.setBounds(35, 180, 200, 30);
        acc_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cnicField.requestFocus();
                }
            }
        });

        cnic = new JLabel("CNIC NUMBER");
        cnic.setBounds(35, 230, 200, 30);

        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter("#####-#######-#");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cnicField = new JFormattedTextField(formatter);
        cnicField.setBounds(35, 260, 200, 30);
        cnicField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String cnic = cnicField.getText().replaceAll("-", "");

                StringBuilder formattedCNIC = new StringBuilder();
                for (int i = 0; i < cnic.length(); i++) {
                    if (i == 5 || i == 12) {
                        formattedCNIC.append("-");
                    }
                    formattedCNIC.append(cnic.charAt(i));
                }
                cnicField.setText(formattedCNIC.toString());
            }
        });

        pay = new JButton();
        pay.setText("Make Payment RS " + totalPrice);
        pay.setBounds(90, 350, 190, 30);
        pay.setBackground(Color.decode("#F2AF13"));
        pay.setForeground(Color.white);
        pay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected_item = (String) jc.getSelectedItem();
                if (selected_item.equalsIgnoreCase("Please select bank")) {
                    JOptionPane.showMessageDialog(null, "Select bank first", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String accountNumber = acc_field.getText();
                    if (validateAccountNumber(accountNumber)) {
                        cnic_num = cnicField.getText();
                        if (!cnic_num.matches("\\d{5}-\\d{7}-\\d")) {
                            JOptionPane.showMessageDialog(null, "Invalid CNIC. Please enter a valid CNIC (e.g., 12345-1234567-1)", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            Bank bank = new Bank(selected_item);
                            if (bank.processPayment(accountNumber, totalPrice)) {
                                dispose();
                                JOptionPane.showMessageDialog(null, "Payment successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                String userName = getUsername();
                                new Booking(userName); // Go to booking or home screen after successful payment
                            } else {
                                JOptionPane.showMessageDialog(null, "Payment failed. Please check your account details and balance.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid account number. It must be 16 digits long and contain only digits.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        text = new JLabel("<html><span style='color:red;'>Attention! Please DO NOT press Back or Refresh this page!<br>Your payment is being processed securely</span></html>");
        text.setBounds(20, 400, 350, 70);

        add(bank);
        add(jc);
        add(acc_num);
        add(acc_field);
        add(cnic);
        add(cnicField);
        add(pay);
        add(text);
        add(back);

        setLayout(null);
        setVisible(true);
    }

    private String getUsername() {
        String username = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("loggedIn.txt"))) {
            username = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception more gracefully in a real application
        }
        return username;
    }

    private boolean validateAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("\\d{16}");
    }
}
