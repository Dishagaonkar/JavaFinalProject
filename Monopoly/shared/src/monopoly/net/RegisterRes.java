package monopoly.net;

public class RegisterRes implements Message {
    private final boolean success;

    public RegisterRes(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() { return success; }
}
