package com.example.controllers;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the home page.
 */
@Controller
public class HomeController {

    private final Firestore db;

    @Autowired
    public HomeController(Firestore db) {
        this.db = db;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            model.addAttribute("profile", principal.getClaims());

            List<String> imenaTurnira = new ArrayList<>();
            String id = principal.getClaims().get("sub").toString();
            db.collection(id).listDocuments().forEach((DocumentReference doc) -> {
                imenaTurnira.add(doc.getId());
            });

            model.addAttribute("imenaTurnira", imenaTurnira);
        }



        return "indexhehe";
    }
}
