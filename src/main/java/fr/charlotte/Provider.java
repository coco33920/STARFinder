package fr.charlotte;

import fr.charlotte.help.DatabaseLite;

import java.util.ArrayList;

public interface Provider {
    void update();
    ArrayList<String> listFromName(String name);

    void load();
    /*
    TODO: Caching
     */
}
