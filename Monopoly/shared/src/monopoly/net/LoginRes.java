package monopoly.net;

public class LoginRes implements Message {
    private final boolean success;

    public LoginRes(boolean success) {
        System.out.println("✅ Login successful.");

        this.success = success;
    }

    public boolean isSuccess() {
        System.out.println("✅ Login successful.");

        return success;
    }
}
