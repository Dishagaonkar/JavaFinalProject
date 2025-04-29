package monopoly.net;

public class LoginRes implements Message {
    private final boolean success;

    public LoginRes(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
