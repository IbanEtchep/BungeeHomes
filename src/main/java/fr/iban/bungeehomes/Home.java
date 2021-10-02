package fr.iban.bungeehomes;

import fr.iban.common.teleport.SLocation;

public class Home {

    private String name;
    private SLocation sLocation;

    public Home(String name, SLocation sLocation) {
        this.name = name;
        this.sLocation = sLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SLocation getSLocation() {
        return sLocation;
    }

    public void setsLocation(SLocation sLocation) {
        this.sLocation = sLocation;
    }
}
