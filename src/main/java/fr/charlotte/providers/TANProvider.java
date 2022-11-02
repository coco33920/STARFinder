package fr.charlotte.providers;

import fr.charlotte.Provider;
import fr.charlotte.help.DatabaseLite;
import fr.charlotte.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TANProvider implements Provider {

    private static TANProvider instance;

    private boolean verbose = false;
    private final DatabaseLite databaseLite;

    public TANProvider(boolean verbose) {
        if (verbose)
            System.out.println("Initializing TAN as provider");
        File databaseFile = this.initializeDatabase(verbose);
        this.databaseLite = new DatabaseLite(databaseFile.getAbsolutePath());
        this.configureDatabases();
        this.verbose = verbose;
        instance = this;
    }

    public TANProvider() {
        this(false);
    }


    public void configureDatabases() {
        if (this.verbose)
            System.out.println("Configuring database for TAN");
        String stopDatabase = "create table if not exists tan_nantes_stops(id integer constraint id primary key autoincrement, nomarret text, lignes text)";
        String thatDatabase = "create table if not exists tan_nantes_lines(id integer constraint id primary key autoincrement, ligne text, arrets text);";
        try {
            this.databaseLite.getConnection().prepareStatement(stopDatabase).execute();
            this.databaseLite.getConnection().prepareStatement(thatDatabase).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray loadStopsRaw() throws IOException {
        File f = new File(getHomeFile().getAbsolutePath() + File.separator + "tan_stops.json");
        String lines = "";
        if (!f.exists()) {
            if (verbose)
                System.out.println("Cache file for TAN does not exists, downloading stops from TAN");
            lines = Utils.readStringFromURL("https://open.tan.fr/ewp/arrets.json");
            boolean b = f.createNewFile();
            if (b) {
                BufferedWriter fw = new BufferedWriter(new FileWriter(f));
                fw.write(lines);
                fw.flush();
                fw.close();
            }
        } else {
            if (verbose)
                System.out.println("Cache file for TAN found, reading from it...");
            BufferedReader br = new BufferedReader(new FileReader(f));
            lines = br.lines().collect(Collectors.joining());
        }
        try {
            return new JSONArray(lines);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public HashMap<String, ArrayList<String>> loadStops(JSONArray array) throws JSONException {
        HashMap<String, ArrayList<String>> stops = new HashMap<>();
        if (array.isEmpty()) {
            return stops;
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String name = obj.getString("libelle");
            JSONArray lignes = obj.getJSONArray("ligne");
            ArrayList<String> lines = new ArrayList<>();
            if (lignes.isEmpty()) {
                stops.put(name, lines);
                continue;
            }
            for (int j = 0; j < lignes.length(); j++) {
                JSONObject o = lignes.getJSONObject(j);
                lines.add(o.getString("numLigne"));
            }
            stops.put(name, lines);
        }
        return stops;
    }

    public ArrayList<String> generateCommandForStops(HashMap<String, ArrayList<String>> stops) {
        ArrayList<String> commands = new ArrayList<>();
        stops.forEach((s, strings) -> {
            commands.add(String.format("insert into tan_nantes_stops(nomarret,lignes) VALUES(\"%s\",\"%s\")", s, String.join(";", strings)));
        });
        return commands;
    }

    public HashMap<String, ArrayList<String>> inverseHashMap(HashMap<String, ArrayList<String>> stops) {
        HashMap<String, ArrayList<String>> inverse = new HashMap<>();
        for (String key : stops.keySet()) {
            ArrayList<String> lines = stops.get(key);
            for(String line : lines){
                if(inverse.containsKey(line)){
                    if(!inverse.get(line).contains(key))
                        inverse.get(line).add(key);
                }else{
                    inverse.put(line,new ArrayList<>());
                    inverse.get(line).add(key);
                }
            }
        }
        return inverse;
    }

    public ArrayList<String> generateCommandForLines(HashMap<String, ArrayList<String>> lines){
        ArrayList<String> commands = new ArrayList<>();
        lines.forEach((s, strings) -> {
            commands.add(String.format("insert into tan_nantes_lines(ligne,arrets) VALUES(\"%s\",\"%s\")",s,String.join(";",strings)));
        });
        return commands;
    }



    public void loadDatabase() throws SQLException, JSONException {
        ResultSet rs = this.databaseLite.getResult("select * from tan_nantes_lines");
        if (!rs.next()) {
            if (verbose)
                System.out.println("Database not loaded... Run");
            try {
                JSONArray jsonArray = loadStopsRaw();
                HashMap<String, ArrayList<String>> stops = loadStops(jsonArray);
                HashMap<String, ArrayList<String>> lines = inverseHashMap(stops);
                ArrayList<String> commands = new ArrayList<>();
                commands.addAll(generateCommandForStops(stops));
                commands.addAll(generateCommandForLines(lines));
                commands.forEach(this.databaseLite::update);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            if (verbose)
                System.out.println("Database already loaded!");
        }
    }


    @Override
    public void load() {
        try {
            loadDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update() {

    }

    @Override
    public ArrayList<String> listOfLinesFromStopName(String name) {
        String statement = "select lignes from tan_nantes_stops where nomarret=\"" + name + "\"";
        return Utils.uniqueGet(this.databaseLite, statement);
    }

    @Override
    public ArrayList<String> listOfStopsFromLineName(String name) {
        String statement = "select nomarret from tan_nantes_stops where lignes like \"%" + name + "%\"";
        return Utils.uniqueGet(databaseLite, statement);
    }

    @Override
    public HashMap<String, ArrayList<String>> listOfConnectionsFromLine(String name) {
        return new HashMap<>();
    }

    @Override
    public String implementationName() {
        return "TAN";
    }

    @Override
    public String tableName() {
        return "lignes";
    }

    @Override
    public String townName() {
        return "Nantes";
    }

    @Override
    public ArrayList<String> executeValue(String endQuest) {
        String statement = "select nomarret from tan_nantes_stops where " + endQuest;
        return Utils.uniqueGet(this.databaseLite, statement);
    }

    @Override
    public ArrayList<String> exposeAllLines() {
        String statement = "select ligne from tan_nantes_lines";
        return Utils.uniqueGet(databaseLite,statement);
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public ArrayList<String> exposeAllStops() {
        String statement = "select nomarret from tan_nantes_stops";
        return Utils.uniqueGet(databaseLite, statement);
    }

    public static TANProvider getInstance(boolean verbose) {
        if (instance == null)
            new TANProvider(verbose);
        instance.setVerbose(verbose);
        return instance;
    }

    public static TANProvider getInstance() {
        return getInstance(false);
    }
}
