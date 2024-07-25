import java.io.*;

public class Bank {
    private String name;

    public Bank(String name) {
        this.name = name;
    }

    public boolean processPayment(String accountNumber, double amount) {
        String bankFile = getBankFileName();
        boolean accountFound = false;
        boolean sufficientBalance = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(bankFile))) {
            StringBuilder updatedContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String accNum = parts[0];
                double balance = Double.parseDouble(parts[1]);

                if (accNum.equals(accountNumber)) {
                    accountFound = true;
                    if (balance >= amount) {
                        sufficientBalance = true;
                        balance -= amount;
                    }
                }

                updatedContent.append(accNum).append(",").append(balance).append(System.lineSeparator());
            }

            if (accountFound && sufficientBalance) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(bankFile))) {
                    writer.write(updatedContent.toString());
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getBankFileName() {
        switch (name) {
            case "Allied Bank":
                return "AlliedBank.txt";
            case "Faysal Bank":
                return "FaysalBank.txt";
            case "Habib Bank Limited":
                return "HabibBankLimited.txt";
            case "Mezan Bank Limited":
                return "MezanBankLimited.txt";
            default:
                throw new IllegalArgumentException("Unknown bank: " + name);
        }
    }
}
    