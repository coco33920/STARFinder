package fr.charlotte.providers;

import fr.charlotte.Provider;
import fr.charlotte.help.DatabaseLite;

import java.sql.SQLException;
import java.util.ArrayList;

public class STARProvider implements Provider {

    private DatabaseLite databaseLite;

    public STARProvider(String file){
        System.out.println("Initializing STAR as Provider");
        System.out.println("File for database is " + file);
        this.databaseLite = new DatabaseLite(file);
        this.configureTable();
    }

    public void configureTable(){
        System.out.println("Configuring database for STAR");
        String statements = "CREATE TABLE IF NOT EXISTS \"star_rennes\" (id INTEGER CONSTRAINT id PRIMARY KEY, name TEXT [NULL | NOT NULL])";
        try {
            databaseLite.getConnection().prepareStatement(statements).execute();
        } catch (Exception e) {
            System.out.println("Creation of the database failed");
            e.printStackTrace();
        }
    }

    public void loadLines(){

    }

    @Override
    public void load() {

    }

    @Override
    public ArrayList<String> listFromName(String name) {
        return new ArrayList<String>();
    }

    @Override
    public void update() {

    }
}
