package monopoly2;

import java.awt.Color;

public class Property extends BoardSpace {
    private int rent;
    private Player owner;
    private Color colorGroup;

    public Property(String name, int rent, Color colorGroup) {
        super(name);
        this.rent = rent;
        this.colorGroup = colorGroup;
    }

    public int getRent() {
        return rent;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isOwned() {
        return owner != null;
    }

    public Color getColorGroup() {
        return colorGroup;
    }

    @Override
    public String getType() {
        return "Property";
    }
}
