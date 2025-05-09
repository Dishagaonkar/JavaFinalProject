package monopoly.net;

import java.io.Serializable;

public class DeclinePropertyReq implements Message, Serializable {
    private static final long serialVersionUID = 1L;
    private final int position;

    public DeclinePropertyReq(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}