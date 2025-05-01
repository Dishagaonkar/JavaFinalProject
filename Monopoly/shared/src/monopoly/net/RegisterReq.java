package monopoly.net;

public class RegisterReq implements Message {
    private final String username;
    private final String password;

    public RegisterReq(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
