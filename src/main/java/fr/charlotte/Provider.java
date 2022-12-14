package fr.charlotte;

import fr.charlotte.help.DatabaseLite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public interface Provider {
    String update();

    ArrayList<String> listOfLinesFromStopName(String name);

    ArrayList<String> listOfStopsFromLineName(String name);

    HashMap<String, ArrayList<String>> listOfConnectionsFromLine(String name);

    String implementationName();

    String tableName();

    String townName();

    ArrayList<String> executeValue(String endQuest);

    ArrayList<String> exposeAllLines();

    ArrayList<String> exposeAllStops();

    default File initializeDatabase(boolean verbose) {
        String home = System.getProperty("user.home");
        String delimiter = File.separator;
        File f = new File(home + delimiter + ".starfinder" + delimiter);
        if (!f.exists())
            f.mkdirs();
        File file = new File(f.getAbsolutePath() + delimiter + implementationName() + ".db");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (verbose)
            System.out.println("Database file loaded to " + file.getAbsolutePath());
        return file;
    }

    default File getHomeFile() {
        String home = System.getProperty("user.home");
        String delimiter = File.separator;
        File f = new File(home + delimiter + ".starfinder" + delimiter + implementationName().toLowerCase() + delimiter);
        if (!f.exists())
            f.mkdirs();
        return f;
    }

    void load();
}
