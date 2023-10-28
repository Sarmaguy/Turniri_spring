package com.example.models;

import java.util.Arrays;
import java.util.List;

import static com.example.models.Utakmica.createRaspored;

public class Turnir {
    public String naziv;
    public List<String> popis;
    public int pobjeda;
    public int nerjeseno;
    public int poraz;
    public String kola;

    public Turnir(String naziv, List<String> popis, int pobjeda, int nerjeseno, int poraz) {
        this.naziv = naziv;
        this.popis = popis;
        this.pobjeda = pobjeda;
        this.nerjeseno = nerjeseno;
        this.poraz = poraz;
    }

    public Turnir(String naziv, List<String> popis, int pobjeda, int nerjeseno, int poraz, String kola) {
        this.naziv = naziv;
        this.popis = popis;
        this.pobjeda = pobjeda;
        this.nerjeseno = nerjeseno;
        this.poraz = poraz;
        this.kola = kola;
    }

    public Turnir() {
    }
}
