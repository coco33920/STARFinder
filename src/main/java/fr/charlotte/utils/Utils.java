package fr.charlotte.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    
    public static <T> ArrayList<T> intersectArrayList(ArrayList<T> a1, ArrayList<T> a2){
        List<T> a = a1.stream().filter(a2::contains).toList();
        return new ArrayList<>(a);
    }
    
}
