package fr.charlotte.providers;

import fr.charlotte.Provider;
import fr.charlotte.help.DatabaseLite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class STARProvider implements Provider {


    @Override
    public String implementationName() {
        return "STAR";
    }

    private final DatabaseLite databaseLite;

    public STARProvider(){
        System.out.println("Initializing STAR as Provider");
        File databaseFile = this.initializeDatabase();
        System.out.println("File for database is " + databaseFile.getName());
        this.databaseLite = new DatabaseLite(databaseFile.getAbsolutePath());
        this.configureTables();
    }

    public void configureTables(){
        System.out.println("Configuring database for STAR");
        String first = "create table if not exists rennes_star_lines(idarret text constraint id primary key,nomarret text,lignes text);";
        String statements = "create table if not exists star_rennes(id integer constraint id primary key autoincrement, name text, aller_id text, retour_id text, other_ways text);";        try {
            databaseLite.getConnection().prepareStatement(first).execute();
            databaseLite.getConnection().prepareStatement(statements).execute();
        } catch (Exception e) {
            System.out.println("Creation of the database failed");
            e.printStackTrace();
        }
    }

    public void loadFileIntoDatabase(String file) throws IOException {
        InputStream is = getClass().getResourceAsStream(file);
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        for(String line; (line = br.readLine()) != null;){
            this.databaseLite.update(line);
        }
    }

    public void loadDatabase() throws SQLException {
        ResultSet rs = this.databaseLite.getResult("SELECT * FROM star_rennes WHERE id=1");
        if(!rs.next()) {
            try {
                loadFileIntoDatabase("/star/lines.sql");
                loadFileIntoDatabase("/star/commands.sql");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("The databases are already charged");
        }
    }

    public void readAllLines() throws SQLException {
        ResultSet rs = this.databaseLite.getResult("SELECT * FROM star_rennes");
        while (rs.next()){
            System.out.println(rs.getString(1)+ ";"+rs.getString(2));
        }
    }
    @Override
    public void load() {
        try {
            loadDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<String> listFromName(String name) {
        try {
            readAllLines();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<String>();
    }

    @Override
    public void update() {

    }
}
