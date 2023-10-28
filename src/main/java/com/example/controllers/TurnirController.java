package com.example.controllers;

import com.example.models.Tim;
import com.example.models.Turnir;
import com.example.models.Utakmica;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class TurnirController {
    private final Firestore db;

    @Autowired
    public TurnirController(Firestore db) {
        this.db = db;
    }

    @GetMapping("/turnir/{turnir}+{sub}")
    public String getTurnirDetails(@PathVariable String turnir, @PathVariable String sub, Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("turnir", turnir);
        model.addAttribute("isOwner", sub != null && principal != null && principal.getClaims().get("sub").toString().equals(sub));

        Turnir t;
        try {
            t = db.collection(sub).document(turnir).get().get().toObject(Turnir.class);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, Integer> timovi = Tim.importTimovi(t.popis);
        List<List<Utakmica>> kola = Utakmica.importRaspored(t.kola);

        for(List<Utakmica> kolo : kola) {
            for(Utakmica utakmica : kolo){
                if (utakmica.ishod == 0){
                    continue;
                }
                else if (utakmica.ishod == 1){
                    timovi.put(utakmica.tim1,timovi.get(utakmica.tim1) + t.pobjeda);
                    timovi.put(utakmica.tim2,timovi.get(utakmica.tim2) + t.poraz);
                }
                else if (utakmica.ishod == 2){
                    timovi.put(utakmica.tim1,timovi.get(utakmica.tim1) + t.poraz);
                    timovi.put(utakmica.tim2,timovi.get(utakmica.tim2) + t.pobjeda);
                }
                else if (utakmica.ishod == 3){
                    timovi.put(utakmica.tim1,timovi.get(utakmica.tim1) + t.nerjeseno);
                    timovi.put(utakmica.tim2,timovi.get(utakmica.tim2) + t.nerjeseno);
                }
            }
        }
        List<Tim> timoviList = Tim.importTimoviList(timovi);

        //remove bye
        for (int i = 0; i < timoviList.size(); i++){
            if (timoviList.get(i).ime.equals("Bye")){
                timoviList.remove(i);
                break;
            }
        }

        timoviList.sort((o1, o2) -> {
            if (o1.bodovi == o2.bodovi) {
                return o1.ime.compareTo(o2.ime);
            }
            return o2.bodovi - o1.bodovi;
        });

        model.addAttribute("timovi", timoviList);


        model.addAttribute("kola", kola);
        return "turnir";
    }

    @PostMapping("/update/{turnir}/{kolo}/{utakmica}")
    public String handleUpdateForm(@PathVariable String turnir,@PathVariable int kolo, @PathVariable int utakmica, Model model, @AuthenticationPrincipal OidcUser principal) {
        String sub = principal.getClaims().get("sub").toString();
        Turnir t;

        try {
            t = db.collection(sub).document(turnir).get().get().toObject(Turnir.class);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        List<List<Utakmica>> kola = Utakmica.importRaspored(t.kola);
        Utakmica u = kola.get(kolo).get(utakmica);

        model.addAttribute("utakmica", u);
        model.addAttribute("kid", kolo);
        model.addAttribute("uid", utakmica);
        model.addAttribute("turnir", turnir);
        String tim1 = "Pobjedio je tim1 - " + u.tim1;
        String tim2 = "Pobjedio je tim2 - " + u.tim2;

        List<String> rezultati = List.of("Neodigrano", "Nerješeno", tim1, tim2);
        model.addAttribute("rezultati", rezultati);
        return "update";
    }

    @PostMapping("/unos")
    public String handleFormSubmission(
            @RequestParam("turnir") String turnir,
            @RequestParam("kolo") String kolo,
            @RequestParam("utakmica") String utakmica,
            @RequestParam("ishod") String ishod,
            @AuthenticationPrincipal OidcUser principal,
            Model model) {

        String sub = principal.getClaims().get("sub").toString();
        Turnir t;

        try {
            t = db.collection(sub).document(turnir).get().get().toObject(Turnir.class);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        List<List<Utakmica>> kola = Utakmica.importRaspored(t.kola);
        Utakmica u = kola.get(Integer.parseInt(kolo)).get(Integer.parseInt(utakmica));

        if (ishod.equals("Neodigrano")){
            u.ishod = 0;
        }
        else if (ishod.equals("Nerješeno")){
            u.ishod = 3;
        }
        else if (ishod.equals("Pobjedio je tim1 - " + u.tim1)){
            u.ishod = 1;
        }
        else if (ishod.equals("Pobjedio je tim2 - " + u.tim2)){
            u.ishod = 2;
        }

        kola.get(Integer.parseInt(kolo)).set(Integer.parseInt(utakmica), u);
        t.kola = Utakmica.exportRaspored(kola);

        try {
            db.collection(sub).document(turnir).set(t).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/turnir/" + turnir + "+" + sub;


    }


}
