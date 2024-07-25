import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;

public class Booking extends JFrame {
    private int width, height;
    private JLabel nameLabel, guestNameLabel, checkInLabel, checkOutLabel, errorLabel1;
    private JTextField guestName, locationField;
    private JDateChooser checkInDateChooser, checkOutDateChooser;
    private JButton searchButton;
    private Font boldFont;

    public Booking(String name) {
        width = 660;
        height = 640;
        setBounds(200, 200, width, height);
        setUndecorated(false);
        setLayout(null);

        // Guest Name Input
        guestNameLabel = new JLabel("Guest Name:");
        guestNameLabel.setBounds(70, 130, 100, 30);
        add(guestNameLabel);

        guestName = new JTextField();
        guestName.setBounds(180, 130, 200, 30);
        guestName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    locationField.requestFocus();
                }
            }
        });
        add(guestName);

        // Location Input
        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setBounds(70, 180, 100, 30);
        add(locationLabel);

        locationField = new JTextField();
        locationField.setBounds(180, 180, 200, 30);
        add(locationField);

        // Check-In Date
        checkInLabel = new JLabel("Check-In Date:");
        checkInLabel.setBounds(70, 230, 100, 30);
        add(checkInLabel);

        checkInDateChooser = new JDateChooser();
        checkInDateChooser.setBounds(180, 230, 150, 30);
        checkInDateChooser.setDate(new Date());
        styleDateChooser(checkInDateChooser);
        checkInDateChooser.addPropertyChangeListener("date", evt -> {
            Date checkInDate = checkInDateChooser.getDate();
            if (checkInDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(checkInDate);
                calendar.add(Calendar.DATE, 1); // Add one day to the check-in date
                checkOutDateChooser.setMinSelectableDate(calendar.getTime());
            }
        });
        add(checkInDateChooser);

        // Check-Out Date
        checkOutLabel = new JLabel("Check-Out Date:");
        checkOutLabel.setBounds(70, 280, 100, 30);
        add(checkOutLabel);

        checkOutDateChooser = new JDateChooser();
        checkOutDateChooser.setBounds(180, 280, 150, 30);
        styleDateChooser(checkOutDateChooser);
        add(checkOutDateChooser);

        nameLabel = new JLabel();
        nameLabel.setText("Hi, " + name + "!");
        nameLabel.setBounds(50, 50, 200, 50);
        nameLabel.setForeground(Color.BLACK);
        int nameLabelFontSize = 18;
        Font nameLabelFont = new Font(nameLabel.getFont().getName(), Font.PLAIN, nameLabelFontSize);
        nameLabel.setFont(nameLabelFont);
        add(nameLabel);

        // Error Labels
        errorLabel1 = new JLabel();
        errorLabel1.setText("*Please fill all the fields!");
        errorLabel1.setBounds(80, height - 150, 300, 50);
        errorLabel1.setForeground(Color.RED);
        boldFont = new Font(errorLabel1.getFont().getName(), Font.BOLD, 12);
        errorLabel1.setFont(boldFont);

        // Search Button
        searchButton = new JButton("Search Rooms");
        searchButton.setBounds(70, 330, 150, 30);
        searchButton.setBackground(Color.decode("#F2AF13"));
        searchButton.setForeground(Color.white);
        boldFont = new Font(searchButton.getFont().getName(), Font.BOLD, 16);
        searchButton.setFont(boldFont);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean showError1 = guestName.getText().isEmpty() || checkInDateChooser.getDate() == null || checkOutDateChooser.getDate() == null;
                boolean showError2 = checkInDateChooser.getDate() != null && checkOutDateChooser.getDate() != null && checkInDateChooser.getDate().equals(checkOutDateChooser.getDate());

                if (showError1) {
                    add(errorLabel1);
                } else {
                    remove(errorLabel1);
                }

                if (showError2) {
                    JOptionPane.showMessageDialog(null, "Check-In and Check-Out dates cannot be the same!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (!showError1) {
                        dispose();
                        new AvailableRooms(checkInDateChooser.getDate(), checkOutDateChooser.getDate(), locationField.getText(), name);
                    }
                }
                repaint();
            }
        });
        add(searchButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleDateChooser(JDateChooser dateChooser) {
        dateChooser.setMinSelectableDate(new Date());
        dateChooser.setFocusable(false);
        dateChooser.setDateFormatString("dd MMM, yyyy");
        JTextFieldDateEditor editor = (JTextFieldDateEditor) dateChooser.getDateEditor();
        styleDateChooserEditor(editor);
    }

    private void styleDateChooserEditor(JTextFieldDateEditor editor) {
        editor.setEditable(false);
        editor.setCursor(null);
    }
}
