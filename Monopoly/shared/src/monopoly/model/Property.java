package monopoly.model;

import java.awt.Color;

public class Property extends BoardSpace {
    private static final long serialVersionUID = 1L;
    private final int rent;
    private Player owner;
    private final Color colorGroup;

    public Property(String name,int rent,Color colorGroup){
        super(name); this.rent=rent; this.colorGroup=colorGroup; }

    public int getRent(){ return rent; }
    public Player getOwner(){ return owner; }
    public void setOwner(Player p){ owner=p; }
    public boolean isOwned(){ return owner!=null; }
    public Color getColorGroup(){ return colorGroup; }

    @Override public String getType(){ return "Property"; }
}
