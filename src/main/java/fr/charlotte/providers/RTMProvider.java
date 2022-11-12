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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class RTMProvider implements Provider {

    private static RTMProvider instance;
    private boolean verbose;
    private final DatabaseLite databaseLite;

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public RTMProvider(boolean verbose){
        instance = this;
        this.verbose = verbose;
        if(verbose)
            System.out.println("Initializing RTM as provider");
        File databaseFile = this.initializeDatabase(verbose);
        this.databaseLite = new DatabaseLite(databaseFile.getAbsolutePath());
        this.initializeDatabase();
    }

    private void initializeDatabase(){
        if (this.verbose)
            System.out.println("Configuring database for RTM");
        String stopDatabase = "create table if not exists rtm_marseilles_stops(id integer constraint id primary key autoincrement, nomarret text, lignes text)";
        String thatDatabase = "create table if not exists rtm_marseilles_lines(id integer constraint id primary key autoincrement, ligne text, arrets text);";
        String connectionsDatabase = "create table if not exists rtm_marseilles_connections(id integer constraint id primary key autoincrement, nomligne text, lignes text);";
        try {
            this.databaseLite.getConnection().prepareStatement(stopDatabase).execute();
            this.databaseLite.getConnection().prepareStatement(thatDatabase).execute();
            this.databaseLite.getConnection().prepareStatement(connectionsDatabase).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public RTMProvider(){
        this(false);
    }

    public static RTMProvider getInstance(boolean verbose) {
        if(instance == null)
            new RTMProvider(verbose);
        instance.setVerbose(verbose);
        return instance;
    }

    @Override
    public String update() {
        return "Not implemented yet";
    }

    @Override
    public ArrayList<String> listOfLinesFromStopName(String name) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> listOfStopsFromLineName(String name) {
        return new ArrayList<>();
    }

    @Override
    public HashMap<String, ArrayList<String>> listOfConnectionsFromLine(String name) {
        return new HashMap<>();
    }

    @Override
    public String implementationName() {
        return "RTM";
    }

    @Override
    public String tableName() {
        return "lignes";
    }

    @Override
    public String townName() {
        return "Marseilles";
    }

    @Override
    public ArrayList<String> executeValue(String endQuest) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> exposeAllLines() {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> exposeAllStops() {
        return new ArrayList<>();
    }
    private JSONObject downloadRTMData(String id,String name) throws IOException {
        File f = new File(getHomeFile().getAbsolutePath() + File.separator + name + ".json");
        String lines = "";
        if (!f.exists()) {
            if (verbose)
                System.out.printf("RTM %s file does not exists, downloading...%n", name);
            if (f.createNewFile()) {
                String url = "https://api.rtm.fr/front/%s/%s".formatted(id,name);
                if(verbose)
                    System.out.printf("URL: %s%n", url);
                lines = Utils.readStringFromURL(url);
                Utils.writeStringToFile(f.getAbsolutePath(), lines);
            }
        } else {
            if (verbose)
                System.out.printf("RTM %s file exists, reading from it%n", name);
            BufferedReader br = new BufferedReader(new FileReader(f));
            lines = br.lines().collect(Collectors.joining());
        }
        try {
            return new JSONObject(lines);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private String loadFirstRoute(String lineId) throws IOException {
        JSONObject routeName = downloadRTMData("getRoutes", lineId);
        JSONObject arr = routeName.getJSONObject("data");
        if(arr.names().length() == 0)
            return "";
        else
           return arr.getJSONObject((String) arr.names().get(0)).getString("refNEtex");
    }

    private HashMap<String,String> routeIdByLineName(JSONObject data) throws IOException {
        HashMap<String,String> res = new HashMap<>();
        for(Object str: data.names().toList()){
            if(str == null)
                continue;
            JSONObject obj = data.getJSONObject((String) str);
            String name = obj.getString("PublicCode");
            String id = obj.getString("id");
            res.put(name,loadFirstRoute(id));
        }
        return res;
    }

    private HashMap<String,String> loadJsonFiles() throws IOException {
        //Raw lines
        JSONObject busLines = downloadRTMData("getLines","bus");
        JSONObject tramLines = downloadRTMData("getLines","tram");
        JSONObject metroLines = downloadRTMData("getLines","metro");

        HashMap<String,String> routeIdByLineName = routeIdByLineName(busLines.getJSONObject("data"));
        routeIdByLineName.putAll(routeIdByLineName(tramLines.getJSONObject("data")));
        routeIdByLineName.putAll(routeIdByLineName(metroLines.getJSONObject("data")));

        return routeIdByLineName;
    }

    private AbstractMap.SimpleImmutableEntry<HashMap<String,ArrayList<String>>,HashMap<String,ArrayList<String>>> generateLinesAndStops(HashMap<String,String> linesIdbyName) throws IOException {
        HashMap<String,ArrayList<String>> lines = new HashMap<>();
        HashMap<String,ArrayList<String>> stops = new HashMap<>();

        for(String key : linesIdbyName.keySet()){
            String value = linesIdbyName.get(key);
            JSONObject raw = downloadRTMData("getStations", value);
            JSONArray data = raw.getJSONArray("data");

            for(int i = 0; i < data.length(); i++){
                JSONObject obj = data.getJSONObject(i);
                String stopName = obj.getString("Name");
                if(!lines.containsKey(key))
                    lines.put(key,new ArrayList<>());
                if(!lines.get(key).contains(stopName))
                    lines.get(key).add(stopName);
                if(!stops.containsKey(stopName))
                    stops.put(stopName,new ArrayList<>());
                if(!stops.get(stopName).contains(key))
                    stops.get(stopName).add(key);
            }
        }
        return new AbstractMap.SimpleImmutableEntry<>(lines,stops);
    }

    private void loadIntoDatabase() throws SQLException, IOException {
        ResultSet rs = databaseLite.getResult("select * from rtm_marseilles_lines");
        if(rs.next()){
            if(verbose)
                System.out.println("Database already loaded!");
           return;
        }
        HashMap<String,String> lineIdByName = loadJsonFiles();
        AbstractMap.SimpleImmutableEntry<HashMap<String,ArrayList<String>>,HashMap<String,ArrayList<String>>> l = generateLinesAndStops(lineIdByName);
        HashMap<String,ArrayList<String>> lines = l.getKey();
        HashMap<String,ArrayList<String>> stops = l.getValue();
        HashMap<String,ArrayList<AbstractMap.SimpleImmutableEntry<String,String>>> connections = Utils.generateConnections(lines,stops);
        ArrayList<String> sqlCommands = new ArrayList<>();
        lines.forEach((s, strings) -> sqlCommands.add("insert into rtm_marseilles_lines(ligne,arrets) VALUES(\"%s\",\"%s\")".formatted(s,String.join(";",strings))));
        stops.forEach((s, strings) -> sqlCommands.add("insert into rtm_marseilles_stops(nomarret,lignes) VALUES(\"%s\",\"%s\")".formatted(s,String.join(";",strings))));
        connections.forEach((s, simpleImmutableEntries) -> {
            String str = simpleImmutableEntries.stream().map(entry -> "(" + entry.getKey() + "," + entry.getValue() + ")").collect(Collectors.joining(";"));
            sqlCommands.add(String.format("insert into rtm_marseilles_connections(nomligne,lignes) VALUES(\"%s\",\"%s\")", s, str));
        });
        sqlCommands.forEach(databaseLite::update);
        if(verbose)
            System.out.println("Database has been updated.");
    }

    @Override
    public void load() {
        try {
            loadIntoDatabase();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
