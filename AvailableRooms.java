import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AvailableRooms extends JFrame {
    private JLabel checkInLabel, checkOutLabel, locationLabel, checkInValueLabel, checkOutValueLabel, locationValueLabel;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private Date selectedCheckInDate, selectedCheckOutDate;
    private String selectedLocation;
    private JButton makeReservationButton;
    private String username;

    public AvailableRooms(Date checkInDate, Date checkOutDate, String location, String username) {
        setUndecorated(true); // Remove the title bar
        this.selectedCheckInDate = checkInDate;
        this.selectedCheckOutDate = checkOutDate;
        this.selectedLocation = location;
        this.username = username;

        setTitle("Room Schedule");
        setSize(660, 640);
        setLayout(new BorderLayout());

        checkInLabel = new JLabel("Check-in Date:");
        checkInValueLabel = new JLabel();
        checkOutLabel = new JLabel("Check-out Date: ");
        checkOutValueLabel = new JLabel();
        locationLabel = new JLabel("Location: ");
        locationValueLabel = new JLabel();

        String[] columnNames = {"Room ID", "Location", "Type", "Availability", "Price Per Night"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(30);
        scheduleTable.setFont(new Font("Arial", Font.PLAIN, 14));
        scheduleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        scheduleTable.getTableHeader().setBackground(Color.RED);

        scheduleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = scheduleTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String[] roomData = new String[tableModel.getColumnCount()];
                        for (int i = 0; i < tableModel.getColumnCount(); i++) {
                            roomData[i] = (String) tableModel.getValueAt(selectedRow, i);
                        }
                        dispose();
                    }
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(checkInLabel);
        panel.add(checkInValueLabel);
        panel.add(checkOutLabel);
        panel.add(checkOutValueLabel);
        panel.add(locationLabel);
        panel.add(locationValueLabel);

        // Add the back button
        JLabel backButton = new JLabel("< Back");
        backButton.setBounds(10, 10, 95, 30);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new Booking(username);
            }
        });

        // Add the back button to a separate panel at the top
        JPanel topPanel = new JPanel();
        topPanel.setLayout(null); // Use absolute layout for precise placement
        topPanel.add(backButton);
        topPanel.setPreferredSize(new Dimension(800, 50));

        // Add the "Make Reservation" button at the bottom
        makeReservationButton = new JButton("Make Reservation");
        makeReservationButton.setBounds(70, 330, 150, 30);
        makeReservationButton.setBackground(Color.decode("#F2AF13"));
        makeReservationButton.setForeground(Color.white);
        Font boldFont = new Font(makeReservationButton.getFont().getName(), Font.BOLD, 16);
        makeReservationButton.setFont(boldFont);
        makeReservationButton.addActionListener(e -> {
            int selectedRow = scheduleTable.getSelectedRow();
            if (selectedRow != -1) {
                double totalPrice = calculateBill(selectedRow);
                JOptionPane.showMessageDialog(this, "Total Bill: Rs" + totalPrice, "Total Bill", JOptionPane.INFORMATION_MESSAGE);
                int dialogResult = JOptionPane.showConfirmDialog(this,
                        "Would you like to proceed with the payment?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if (dialogResult == JOptionPane.YES_OPTION) {
                    dispose();
                    new Payment(totalPrice, selectedCheckInDate, selectedCheckOutDate, selectedLocation, username);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a room to make a reservation.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(makeReservationButton, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH); // Add the top panel to the frame
        add(panel, BorderLayout.CENTER); // Add the panel to the center of the frame
        add(new JScrollPane(scheduleTable), BorderLayout.CENTER); // Add the table to the center of the frame
        add(bottomPanel, BorderLayout.SOUTH); // Add the bottom panel with the button

        setSelectedDates(selectedCheckInDate, selectedCheckOutDate);
        locationValueLabel.setText(selectedLocation);

        setLocationRelativeTo(null);
        setVisible(true);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        displayRoomsForDates(selectedCheckInDate, selectedCheckOutDate, selectedLocation);
    }

    public void setSelectedDates(Date checkInDate, Date checkOutDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        checkInValueLabel.setText(dateFormat.format(checkInDate));
        checkOutValueLabel.setText(dateFormat.format(checkOutDate));
    }

    private void displayRoomsForDates(Date checkInDate, Date checkOutDate, String location) {
        List<String[]> rooms = getRoomsForDates(checkInDate, checkOutDate, location);
        tableModel.setRowCount(0); // Clear existing data
        if (rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No rooms available for the selected dates.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (String[] room : rooms) {
                tableModel.addRow(room); // Add the room data to the table model
            }
        }
    }

    private List<String[]> getRoomsForDates(Date checkInDate, Date checkOutDate, String location) {
        List<String[]> rooms = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader("Rooms.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) { // Check if the line has 5 parts (including price per night)
                    String roomLocation = parts[1];
                    String availability = parts[3];
                    String pricePerNight = parts[4]; // Get price per night

                    // Check if the location contains the specified search term
                    if (roomLocation.toLowerCase().contains(location.toLowerCase()) && isRoomAvailable(availability, checkInDate, checkOutDate)) {
                        // Add price per night to room data
                        String[] roomData = {parts[0], roomLocation, parts[2], availability, pricePerNight};
                        rooms.add(roomData);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading rooms file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return rooms;
    }

    private boolean isRoomAvailable(String availability, Date checkInDate, Date checkOutDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Split availability string into booked periods
            String[] bookedPeriods = availability.split(";");

            for (String period : bookedPeriods) {
                // Each period is in the format "startDate-endDate"
                String[] dates = period.split("-");
                if (dates.length == 2) {
                    Date bookedStartDate = dateFormat.parse(dates[0]);
                    Date bookedEndDate = dateFormat.parse(dates[1]);

                    // Check if there is an overlap
                    if ((checkInDate.before(bookedEndDate) && checkOutDate.after(bookedStartDate)) ||
                            (checkInDate.equals(bookedStartDate) || checkOutDate.equals(bookedEndDate))) {
                        return false; // Room is not available
                    }
                }
            }

            // If no overlap is found, the room is available
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error parsing dates: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private double calculateBill(int selectedRow) {
        String pricePerNightStr = (String) tableModel.getValueAt(selectedRow, 4);
        double pricePerNight = Double.parseDouble(pricePerNightStr);

        long diffInMillis = selectedCheckOutDate.getTime() - selectedCheckInDate.getTime();
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

        return pricePerNight * diffInDays;
    }
}
