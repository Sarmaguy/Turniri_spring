package com.example.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tim {
    public String ime;
    public int bodovi;

    public Tim(String ime, int bodovi) {
        this.ime = ime;
        this.bodovi = bodovi;
    }


    public static HashMap<String, Integer> importTimovi(List<String> popis) {
        HashMap<String, Integer> timovi = new HashMap<>();
        for (String tim : popis) {
            timovi.put(tim, 0);
        }
        return timovi;
    }

    public static List<Tim> importTimoviList(HashMap<String, Integer> timovi) {
        List<Tim> timoviList = new ArrayList<>();
        for (String tim : timovi.keySet()) {
            timoviList.add(new Tim(tim, timovi.get(tim)));
        }
        return timoviList;
    }
}
