package monopoly2;

import java.awt.Color;


public class Railroad extends Property {
    public Railroad(String name) {
        super(name, 25, Color.BLACK);
    }

    @Override public String getType() { return "Railroad"; }
}