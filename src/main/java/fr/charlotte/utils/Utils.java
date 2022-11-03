package fr.charlotte.utils;

import fr.charlotte.help.DatabaseLite;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
