package fr.charlotte.providers;

import fr.charlotte.Provider;
import fr.charlotte.help.DatabaseLite;
import fr.charlotte.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class TCLProvider implements Provider {

    private static TCLProvider instance;

    private boolean verbose = false;
    private final DatabaseLite databaseLite;

    public TCLProvider(boolean verbose) {
        this.verbose = verbose;
        instance = this;
        File databaseFile = this.initializeDatabase(verbose);
        this.databaseLite = new DatabaseLite(databaseFile.getAbsolutePath());
        initializeDatabase();
    }

    private void initializeDatabase() {
        if (this.verbose)
            System.out.println("Configuring database for TCL");
        String stopDatabase = "create table if not exists tcl_lyon_stops(id integer constraint id primary key autoincrement, nomarret text, lignes text)";
        String thatDatabase = "create table if not exists tcl_lyon_lines(id integer constraint id primary key autoincrement, ligne text, arrets text);";
        String connectionsDatabase = "create table if not exists tcl_lyon_connections(id integer constraint id primary key autoincrement, nomligne text, lignes text);";
        try {
            this.databaseLite.getConnection().prepareStatement(stopDatabase).execute();
            this.databaseLite.getConnection().prepareStatement(thatDatabase).execute();
            this.databaseLite.getConnection().prepareStatement(connectionsDatabase).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private JSONObject downloadTCLData(String name, String id) throws IOException {
        File f = new File(getHomeFile().getAbsolutePath() + File.separator + name + ".json");
        String lines = "";
        if (!f.exists()) {
            if (verbose)
                System.out.printf("TCL %s file does not exists, downloading...%n", name);
            if (f.createNewFile()) {
                lines = Utils.readStringFromURL("https://download.data.grandlyon.com/ws/rdata/%s/all.json?maxfeatures=-1&start=1".formatted(id));
                Utils.writeStringToFile(f.getAbsolutePath(), lines);
            }
        } else {
            if (verbose)
                System.out.printf("TCL %s file exists, reading from it%n", name);
            BufferedReader br = new BufferedReader(new FileReader(f));
            lines = br.lines().collect(Collectors.joining());
        }
        try {
            return new JSONObject(lines);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private void extractIdFromJSONObject(HashMap<String, String> map, JSONObject object) {
        JSONArray metroArray = object.getJSONArray("values");
        for (int i = 0; i < metroArray.length(); i++) {
            JSONObject obj = metroArray.getJSONObject(i);
            if (!map.containsKey(obj.getString("code_ligne"))) {
                map.put(obj.getString("code_ligne"), obj.getString("ligne"));
            }
        }
    }

    private HashMap<String, String> linesFromId() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        JSONObject metroObject = downloadTCLData("tcl_metro", "tcl_sytral.tcllignemf_2_0_0");
        JSONObject tramObject = downloadTCLData("tcl_tram", "tcl_sytral.tcllignetram_2_0_0");
        JSONObject busObject = downloadTCLData("tcl_bus", "tcl_sytral.tcllignebus_2_0_0");
        extractIdFromJSONObject(map, metroObject);
        extractIdFromJSONObject(map, tramObject);
        extractIdFromJSONObject(map, busObject);
        return map;
    }

    private void putIfNotExists(String toPut, String value, HashMap<String, ArrayList<String>> values) {
        if (!values.containsKey(toPut))
            values.put(toPut, new ArrayList<>());
        values.get(toPut).add(value);
    }

    private AbstractMap.SimpleImmutableEntry<HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>> calculateFromStopsAndFromLines() throws IOException {
        HashMap<String, ArrayList<String>> lines = new HashMap<>();
        HashMap<String, ArrayList<String>> stops = new HashMap<>();

        HashMap<String, String> linesById = linesFromId();


        JSONObject jsonStops = downloadTCLData("tcl_stops", "tcl_sytral.tclarret");
        JSONArray array = jsonStops.getJSONArray("values");
        for (int i = 0; i < array.length(); i++) {
            String name = array.getJSONObject(i).getString("nom");
            String desserteRaw = array.getJSONObject(i).getString("desserte");
            ArrayList<String> linesDessertes = new ArrayList<>();

            for (String s : desserteRaw.split(",")) {
                String str = s.substring(0,s.length()-2).trim();
                String b = linesById.get(str);
                if(b==null) {
                    continue;
                }
                if(!linesDessertes.contains(b)){
                    linesDessertes.add(b);
                }
            }

            if(!stops.containsKey(name))
                stops.put(name,new ArrayList<>());
            for(String s : linesDessertes){
                if(stops.get(name).contains(s))
                    continue;
                stops.get(name).add(s);
            }
            linesDessertes.forEach(s -> putIfNotExists(s, name, lines));
        }
        return new AbstractMap.SimpleImmutableEntry<>(stops, lines);
    }

    private void loadDatabase() throws IOException, SQLException {
        ResultSet rs = databaseLite.getResult("select * from tcl_lyon_stops");
        if (!rs.next()) {
            if (verbose)
                System.out.println("database empty.. loading in");
            AbstractMap.SimpleImmutableEntry<HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>> maps = calculateFromStopsAndFromLines();
            HashMap<String, ArrayList<String>> stopList = maps.getKey();
            HashMap<String, ArrayList<String>> lineList = maps.getValue();
            HashMap<String, ArrayList<AbstractMap.SimpleImmutableEntry<String, String>>> connections = Utils.generateConnections(lineList, stopList);
            HashMap<String, String> stops = new HashMap<>();
            HashMap<String, String> lines = new HashMap<>();

            stopList.forEach((s, strings) -> stops.put(s, String.join(";", strings)));
            lineList.forEach((s, strings) -> lines.put(s, String.join(";", strings)));

            ArrayList<String> stopsCommands = new ArrayList<>();
            ArrayList<String> linesCommand = new ArrayList<>();
            ArrayList<String> connectionCommands = new ArrayList<>();

            stops.forEach((s, s2) -> stopsCommands.add("insert into tcl_lyon_stops(nomarret,lignes) VALUES(\"%s\",\"%s\")".formatted(s, s2)));
            lines.forEach((s, s2) -> linesCommand.add("insert into tcl_lyon_lines(ligne,arrets) VALUES(\"%s\",\"%s\")".formatted(s, s2)));

            connections.forEach((s, simpleImmutableEntries) -> {
                String str = simpleImmutableEntries.stream().map(entry -> "(" + entry.getKey() + "," + entry.getValue() + ")").collect(Collectors.joining(";"));
                connectionCommands.add(String.format("insert into tcl_lyon_connections(nomligne,lignes) VALUES(\"%s\",\"%s\")", s, str));
            });

            stopsCommands.forEach(databaseLite::update);
            linesCommand.forEach(databaseLite::update);
            connectionCommands.forEach(databaseLite::update);
            if (verbose)
                System.out.println("Database loaded!");
        } else {
            if (verbose)
                System.out.println("Database already loaded");
        }
    }


    @Override
    public String update() {
        File f = new File(getHomeFile().getAbsolutePath() + File.separator + "tcl_metro.json");
        File f2 = new File(getHomeFile().getAbsolutePath() + File.separator + "tcl_tram.json");
        File f3 = new File(getHomeFile().getAbsolutePath() + File.separator + "tcl_bus.json");
        File f4 = new File(getHomeFile().getAbsolutePath() + File.separator + "tcl_stops.json");
        String statement = "delete from tcl_lyon_lines";
        String s2 = "delete from tcl_lyon_stops";
        String s3 = "delete from tcl_lyon_connections";
        if (f.exists())
            f.delete();
        if(f2.exists())
            f2.delete();
        if(f3.exists())
            f3.delete();
        if(f4.exists())
            f4.delete();
        databaseLite.update(statement);
        databaseLite.update(s2);
        databaseLite.update(s3);
        this.initializeDatabase();
        this.load();
        return "Update finished with success";
    }

    @Override
    public ArrayList<String> listOfLinesFromStopName(String name) {
        String statement = "select lignes from tcl_lyon_stops where nomarret=\"" + name + "\"";
        String s = (String) databaseLite.read(statement, "lignes");
        return new ArrayList<>(Arrays.asList(s.split(";")));
    }

    @Override
    public ArrayList<String> listOfStopsFromLineName(String name) {
        String statement = "select arrets from tcl_lyon_lines where ligne = \"" + name + "\"";
        String s = (String) databaseLite.read(statement, "arrets");
        return new ArrayList<>(Arrays.asList(s.split(";")));
    }

    @Override
    public HashMap<String, ArrayList<String>> listOfConnectionsFromLine(String name) {
        HashMap<String, ArrayList<String>> result = new HashMap<>();
        String statement = "select lignes from tcl_lyon_connections where nomligne=\"" + name + "\"";
        ArrayList<String> s = Utils.uniqueGet(databaseLite, statement);
        for (String s1 : s) {
            String[] sl = s1.split(";");
            for (String l : sl) {
                String[] v = l.split(",");
                String key = v[0].replace("(", "");
                String value = v[1].replace(")", "");
                if (result.containsKey(key)) {
                    result.get(key).add(value);
                } else {
                    result.put(key, new ArrayList<>());
                    result.get(key).add(value);
                }
            }
        }
        return result;
    }

    @Override
    public String implementationName() {
        return "TCL";
    }

    @Override
    public String tableName() {
        return "lignes";
    }

    @Override
    public String townName() {
        return "Lyon";
    }

    @Override
    public ArrayList<String> executeValue(String endQuest) {
        String statement = "select nomarret from tcl_lyon_stops where %s".formatted(endQuest);
        return Utils.uniqueGet(databaseLite, statement);
    }

    @Override
    public ArrayList<String> exposeAllLines() {
        String statement = "select ligne from tcl_lyon_lines";
        return Utils.uniqueGet(databaseLite, statement);
    }

    @Override
    public ArrayList<String> exposeAllStops() {
        String statement = "select nomarret from tcl_lyon_stops";
        return Utils.uniqueGet(databaseLite, statement);
    }

    @Override
    public void load() {
        try {
            loadDatabase();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public static TCLProvider getInstance(boolean verbose) {
        if (instance == null)
            new TCLProvider(verbose);
        return instance;
    }
}
