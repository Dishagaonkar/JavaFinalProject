package monopoly.model;

import java.io.Serializable;

public abstract class BoardSpace implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;

    protected BoardSpace(String name) { this.name = name; }
    public String getName()           { return name; }
    public abstract String getType();
}