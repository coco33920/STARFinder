package fr.charlotte.providers;

import fr.charlotte.Provider;
import fr.charlotte.help.DatabaseLite;
import fr.charlotte.utils.Utils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TCLProvider implements Provider {

    private static TCLProvider instance;

    private boolean verbose = false;
    private final DatabaseLite databaseLite;

    public TCLProvider(boolean verbose){
        this.verbose = verbose;
        instance = this;
        File databaseFile = this.initializeDatabase(verbose);
        this.databaseLite = new DatabaseLite(databaseFile.getAbsolutePath());
        initializeDatabase();
    }


    private JSONObject downloadJsonMetro() throws IOException {
        File f = new File(getHomeFile().getAbsolutePath() + File.separator + "tcl-metro.json");
        String lines = "";
        if(!f.exists()){
            if(verbose)
                System.out.println("TCL metro file does not exists, downloading...");
            if(f.createNewFile()){
                lines = Utils.readStringFromURL("https://download.data.grandlyon.com/ws/rdata/tcl_sytral.tcllignemf_2_0_0/all.json?maxfeatures=-1&start=1");
                Utils.writeStringToFile(f.getAbsolutePath(),lines);
            }
        }else{
            if(verbose)
                System.out.println("TCL metro file exists, reading from it");
            BufferedReader br = new BufferedReader(new FileReader(f));
            lines = br.lines().collect(Collectors.joining());
        }
        try{
            return new JSONObject(lines);
        }catch (Exception e){
            return new JSONObject();
        }
    }


    private void initializeDatabase(){

    }

    @Override
    public String update() {
        return null;
    }

    @Override
    public ArrayList<String> listOfLinesFromStopName(String name) {
        return null;
    }

    @Override
    public ArrayList<String> listOfStopsFromLineName(String name) {
        return null;
    }

    @Override
    public HashMap<String, ArrayList<String>> listOfConnectionsFromLine(String name) {
        return null;
    }

    @Override
    public String implementationName() {
        return "TCL";
    }

    @Override
    public String tableName() {
        return null;
    }

    @Override
    public String townName() {
        return "Lyon";
    }

    @Override
    public ArrayList<String> executeValue(String endQuest) {
        return null;
    }

    @Override
    public ArrayList<String> exposeAllLines() {
        return null;
    }

    @Override
    public ArrayList<String> exposeAllStops() {
        return null;
    }

    @Override
    public void load() {
        try {
            JSONObject jsonObject = downloadJsonMetro();
            System.out.println("t");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TCLProvider getInstance(boolean verbose) {
        if (instance == null)
            new TCLProvider(verbose);
        return instance;
    }
}
