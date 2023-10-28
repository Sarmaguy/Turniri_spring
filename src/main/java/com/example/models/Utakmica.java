package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Utakmica {
    public boolean odigrana;
    public int ishod;
    public String tim1;
    public String tim2;
    public boolean bye;

    public Utakmica(String tim1, String tim2) {
        this.tim1 = tim1;
        this.tim2 = tim2;
        this.odigrana = false;
        this.ishod = 0;
    }

    public static void main(String[] args) {
        List<String> popis = new ArrayList<String>();
        popis.add("popis");
        popis.add("popis2");
        popis.add("popis3");
        //popis.add("popis4");
        //popis.add("popis5");

        createRaspored(popis);
        System.out.println(exportRaspored(createRaspored(popis)));
    }

    public static List<List<Utakmica>> createRaspored(List<String> listTeam) {
        String byeChecker = "Bye";
        if (listTeam.size() % 2 != 0) {
            listTeam.add(byeChecker); // If odd number of teams add a dummy
        }

        int numDays = listTeam.size() - 1; // Days needed to complete tournament
        int halfSize = listTeam.size() / 2;

        List<String> teams = new ArrayList<>(listTeam);

        teams.remove(0); // Remove the first team

        int teamsSize = teams.size();

        List<List<Utakmica>> kola = new ArrayList<List<Utakmica>>();

        for (int day = 0; day < numDays; day++) {
            List<Utakmica> kolo = new ArrayList<Utakmica>();
            System.out.println("Day " + (day + 1));

            int teamIdx = day % teamsSize;

            Utakmica u = new Utakmica(listTeam.get(0), teams.get(teamIdx));
            createUtakmica(byeChecker, (List<Utakmica>) kolo, u);

            for (int idx = 1; idx < halfSize; idx++) {
                int firstTeam = (day + idx) % teamsSize;
                int secondTeam = (day + teamsSize - idx) % teamsSize;
                System.out.println(teams.get(firstTeam) + " vs " + teams.get(secondTeam));
                u = new Utakmica(teams.get(firstTeam), teams.get(secondTeam));
                createUtakmica(byeChecker, (List<Utakmica>) kolo, u);
            }
            kola.add(kolo);
        }
        return kola;
    }

    private static void createUtakmica(String byeChecker, List<Utakmica> kolo, Utakmica u) {
        if (u.tim2 == byeChecker || u.tim1 == byeChecker) {
            u.bye = true;
            if (u.tim1 == byeChecker) {
                u.tim1 = u.tim2;
                u.tim2 = "Bye";
            }
        }
        kolo.add(u);
    }

    public static String exportRaspored(List<List<Utakmica>> kola) {
        String s = "";
        for (List<Utakmica> kolo : kola) {
            for (Utakmica utakmica : kolo) {
                s+=utakmica.tim1 + "-"+ String.valueOf(utakmica.ishod)+"-" + utakmica.tim2+",";

            }
            s = s.substring(0, s.length() - 1);
            s+=";";
        }
        s = s.substring(0, s.length() - 1);
        return s;
    }

    public static List<List<Utakmica>> importRaspored(String s){
        List<List<Utakmica>> kola = new ArrayList<List<Utakmica>>();
        String[] kolo = s.split(";");
        for (String string : kolo) {
            List<Utakmica> utakmice = new ArrayList<Utakmica>();
            String[] utakmica = string.split(",");
            for (String string2 : utakmica) {
                String[] timovi = string2.split("-");
                Utakmica u = new Utakmica(timovi[0], timovi[2]);
                if (timovi[2].equals("Bye"))
                    u.bye = true;
                u.ishod = Integer.parseInt(timovi[1]);
                utakmice.add(u);
            }
            kola.add(utakmice);
        }
        return kola;
    }

    @Override
    public String toString() {
        String s = "";
        if (this.bye) {
            return this.tim1 + " - " + "Bye";
        } else {
            s = this.tim1 + " - " + this.tim2;
        }
       if (ishod == 0)
           s += "   (utakmica je neodigrana)";
       else if (ishod == 3)
              s += "\tUtakmica je zavrsila nerijeseno";
       else
           s += ishod == 1 ? "\tPobjedio je: " + tim1 : "\tPobjedio je: " + tim2;
        return s;
    }
}
