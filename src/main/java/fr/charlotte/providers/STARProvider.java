package fr.charlotte.providers;

import fr.charlotte.Provider;
import fr.charlotte.help.DatabaseLite;
import org.sqlite.SQLiteException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class STARProvider implements Provider {


    @Override
    public String implementationName() {
        return "STAR";
    }

    private final DatabaseLite databaseLite;

    public STARProvider(){
        System.out.println("Initializing STAR as Provider");
        File databaseFile = this.initializeDatabase();
        this.databaseLite = new DatabaseLite(databaseFile.getAbsolutePath());
        this.configureTables();
    }

    public void configureTables(){
        System.out.println("Configuring database for STAR");
        String first = "create table if not exists rennes_star_lines(id integer constraint id primary key autoincrement ,nomarret text,lignes text);";
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
    public String townName() {
        return "Rennes";
    }

    public ArrayList<String> uniqueGet(String statement){
        ResultSet rs = this.databaseLite.getResult(statement);
        if(rs == null){
            return new ArrayList<>();
        }
        ArrayList<String> result = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                String n = rs.getString(1);
                if(!result.contains(n))
                    result.add(n);
            }catch (Exception e) {
                return new ArrayList<>();
            }
        }
        return result;
    }

    @Override
    public ArrayList<String> listOfLinesFromStopName(String name) {
        String statement = String.format(
                "SELECT lignes FROM rennes_star_lines WHERE nomarret=\"%s\"", name
        );
        ArrayList<String> s = uniqueGet(statement);
        ArrayList<String> result = new ArrayList<>();
        s.forEach(s1 -> result.addAll(Arrays.stream(s1.split(";")).toList()));
        return result;
    }

    @Override
    public ArrayList<String> listOfStopsFromLineName(String name) {
        String statement = "select nomarret from rennes_star_lines where lignes like \"%"+name+"%\"";
        ArrayList<String> s = uniqueGet(statement);
        ArrayList<String> result = new ArrayList<>();
        s.forEach(s1 -> result.addAll(Arrays.stream(s1.split(";")).toList()));
        return result;
    }

    @Override
    public ArrayList<String> exposeAllLines() {
        String statement = "select name from star_rennes";
        ResultSet rs = databaseLite.getResult(statement);
        if(rs == null)
            return new ArrayList<>();
        ArrayList<String> a = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                a.add(rs.getString(1));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return a;
    }

    @Override
    public ArrayList<String> exposeAllStops() {
        String statement = "select nomarret from rennes_star_lines";
        ResultSet rs = databaseLite.getResult(statement);
        if(rs == null)
            return new ArrayList<>();
        ArrayList<String> a = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                a.add(rs.getString(1));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return a;
    }

    @Override
    public String tableName() {
        return "lignes";
    }

    @Override
    public ArrayList<String> executeValue(String endQuest) {
        String statement = "select nomarret from rennes_star_lines where " + endQuest;
        return uniqueGet(statement);
    }

    @Override
    public void update() {

    }
}
