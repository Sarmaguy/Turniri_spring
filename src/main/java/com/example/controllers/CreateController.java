package com.example.controllers;

import com.example.models.Turnir;
import com.example.models.Utakmica;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.models.Utakmica.createRaspored;
import static com.example.models.Utakmica.exportRaspored;

@Controller
public class CreateController {

    private final Firestore db;

    @Autowired
    public CreateController(Firestore db) {
        this.db = db;
    }


    @GetMapping("/create")
    public String create() {
        return "create";
    }

    @PostMapping("/create")
    public String processForm(
            @RequestParam String naziv,
            @RequestParam String popis,
            @RequestParam int pobjeda,
            @RequestParam int nerjeseno,
            @RequestParam int poraz,
            @AuthenticationPrincipal OidcUser principal, Model model){

        String id = principal.getClaims().get("sub").toString();
        List<String> igraci = new java.util.ArrayList<>(List.of(popis.split("[\r\n;]")));

        igraci.removeIf(String::isBlank);

        if (igraci.size() < 4 || igraci.size() >8){
            model.addAttribute("error", "Broj igrača mora biti između 4 i 8!");
            model.addAttribute("naziv", naziv);
            model.addAttribute("popis", popis);
            model.addAttribute("pobjeda", pobjeda);
            model.addAttribute("nerjeseno", nerjeseno);
            model.addAttribute("poraz", poraz);
            return "create";
        }
        //check if names is already taken
        try {
            if (db.collection(id).document(naziv).get().get().exists()){
                model.addAttribute("error", "Stvorili ste turnir s istim imenom!");
                model.addAttribute("naziv", naziv);
                model.addAttribute("popis", popis);
                model.addAttribute("pobjeda", pobjeda);
                model.addAttribute("nerjeseno", nerjeseno);
                model.addAttribute("poraz", poraz);
                return "create";
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        Turnir t = new Turnir(naziv, igraci, pobjeda, nerjeseno, poraz);

        t.kola = exportRaspored(createRaspored(igraci));

        ApiFuture<WriteResult> a = db.collection(id).document(naziv).set(t);

        String url = "/turnir/"+naziv +"+"+id;

        return "redirect:"+url;

    }
}
