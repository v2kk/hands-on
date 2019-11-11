package vn.fpt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Register {
    String date = "";
    int userId = -1;

    public Register(String date, int userId) {
        this.date = date;
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

public class NewUsersJava {
    public static void main(String[] args) {

        String filePath = "./input/stackoverflow_users.csv";

        try(Stream<String> linesStream = Files.lines(Paths.get(filePath))) {
            List<String> lines                                  = linesStream.collect(Collectors.toList());
            List<Register> registerLst                          = extractRegisterDate(lines);
            Map<String, Integer> registerByDateMp               = countNewUsers(registerLst);
            ArrayList<Map.Entry<String, Integer>> sorted        = sortByDate(registerByDateMp);

            sorted.subList(0, 20).forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static List<Register> extractRegisterDate(List<String> lines) {
        List<Register> registerLst = new ArrayList<Register>();

        for(int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] arr = line.split(",");

            String date = arr[1].substring(0, 10);
            int userId = Integer.parseInt(arr[0]);
            registerLst.add(new Register(date, userId));
        }

        return registerLst;
    }

    private static Map<String, Integer> countNewUsers(List<Register> registerLst) {
        Map<String, Integer> registerByDateMp = new LinkedHashMap<String, Integer>();

        for(int i = 0; i < registerLst.size(); i++) {
            String date = registerLst.get(i).getDate();
            int tempNumber = registerByDateMp.getOrDefault(date, 0);

            registerByDateMp.put(date, tempNumber + 1);
        }

        return registerByDateMp;
    }

    private static ArrayList<Map.Entry<String, Integer>> sortByDate(Map<String, Integer> registerByDateMp) {
        ArrayList<Map.Entry<String, Integer>> sorted = new ArrayList<>(registerByDateMp.entrySet());
        sorted.sort(Collections.reverseOrder(Comparator.comparing(Map.Entry::getKey)));

        return sorted;
    }
}