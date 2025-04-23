package monopoly.model;

import java.awt.Color;


public class Utility extends Property {
    public Utility(String name) {
        super(name, 75, Color.GRAY);
    }

    @Override public String getType() { return "Utility"; }
}
