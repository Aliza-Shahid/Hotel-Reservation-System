
public class User {
    private String name;
    private String phoneNumber;
    private String password;
    private String securityQuestion;
    private String answer;

    public User(String name, String phoneNumber, String password, String securityQuestion, String answer) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.answer = answer;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getAnswer() {
        return answer;
    }
    public String toString() {
        return name + "," + phoneNumber + "," + password + "," + securityQuestion + "," + answer;
    }

    public static User fromString(String userData) {
        String[] data = userData.split(",");
        if (data.length != 5) {
            throw new IllegalArgumentException("Invalid user data format");
        }
        return new User(data[0], data[1], data[2], data[3], data[4]);
    }
}
