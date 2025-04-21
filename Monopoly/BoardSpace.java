package monopoly2;

import java.awt.Color;

public abstract class BoardSpace {
    protected String name;

    public BoardSpace(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getType();
}
