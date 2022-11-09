package fr.charlotte.utils;

import fr.charlotte.help.DatabaseLite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static <T> ArrayList<T> intersectArrayList(ArrayList<T> a1, ArrayList<T> a2) {
        List<T> a = a1.stream().filter(a2::contains).toList();
        return new ArrayList<>(a);
    }

    public static String readStringFromURL(String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8)) {
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                String s = scanner.next();
                scanner.close();
                return s;
            } else {
                scanner.close();
                return "";
            }
        }
    }
    
    public static boolean writeStringToFile(String filename,String lines) throws IOException {
        File f = new File(filename);
        boolean b;
        if(!f.exists()){
            b = f.createNewFile();
            if(!b)
                return false;
        }
        BufferedWriter fw = new BufferedWriter(new FileWriter(f));
        fw.write(lines);
        fw.flush();
        fw.close();
        return true;
    }


    public static HashMap<String, ArrayList<AbstractMap.SimpleImmutableEntry<String, String>>> generateConnections(HashMap<String, ArrayList<String>> lines, HashMap<String, ArrayList<String>> stops) {
        HashMap<String, ArrayList<AbstractMap.SimpleImmutableEntry<String, String>>> connections = new HashMap<>();

        for (String line : lines.keySet()) {
            ArrayList<String> stopLine = lines.get(line);
            for (String stop : stopLine) {
                ArrayList<String> linesFromStop = stops.get(stop);
                for (String l : linesFromStop) {
                    if (!connections.containsKey(line))
                        connections.put(line, new ArrayList<>());
                    connections.get(line).add(new AbstractMap.SimpleImmutableEntry<>(l, stop));
                }
            }
        }

        return connections;
    }

    public static ArrayList<String> uniqueGet(DatabaseLite databaseLite, String statement) {
        ResultSet rs = databaseLite.getResult(statement);
        if (rs == null) {
            return new ArrayList<>();
        }
        ArrayList<String> result = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                String n = rs.getString(1);
                if (!result.contains(n))
                    result.add(n);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
        return result;
    }

}
