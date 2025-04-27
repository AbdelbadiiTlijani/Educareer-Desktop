package tn.esprit.educareer.server;

import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.models.ParticipationProjet;

import tn.esprit.educareer.services.ServiceCategorieProjet;
import tn.esprit.educareer.services.ServiceParticipationProjet;
import tn.esprit.educareer.services.ServiceProjet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Timer;
import java.util.TimerTask;

public class FusionServer {

    private final ServiceProjet projetService = new ServiceProjet();
    private final ServiceParticipationProjet participationProjet = new ServiceParticipationProjet();
    private final ServiceCategorieProjet categorieService = new ServiceCategorieProjet();

    public void start() {
        Timer timer = new Timer();

        // Schedule the task every 3 minutes (1000 ms)
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mergeProjects();
            }
        }, 0, 60000);
    }

    // ➔ Juste changer private en public pour pouvoir l'appeler
    public void mergeProjects() {
        System.out.println("Checking for projects to merge...");

        try {
            List<Projet> allProjects = projetService.getAll();
            Map<Integer, List<Projet>> projectsToMerge = new HashMap<>();

            for (Projet projet : allProjects) {
                int participantCount = ServiceProjet.getNombreParticipants(projet.getId());
                if (participantCount <= 1) {
                    projectsToMerge.computeIfAbsent(projet.getCategorie_id(), k -> new ArrayList<>()).add(projet);
                }
            }

            for (Map.Entry<Integer, List<Projet>> entry : projectsToMerge.entrySet()) {
                if (entry.getValue().size() > 1) {
                    mergeProjetList(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            System.err.println("Error during project merge: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mergeProjetList(int categorieId, List<Projet> projets) {
        try {
            CategorieProjet categorie = ServiceProjet.getCategorieById(categorieId);

            String mergedTitle = "Pack " + categorie.getCategorie();
            String mergedDescription = "Fusion des projets de la catégorie: " + categorie.getCategorie();

            String mergedContenu = projets.stream()
                    .map(Projet::getContenu)
                    .collect(Collectors.joining("\n---\n")); // Separate with "---"

            // Choisir un formateur d'un des projets (ex: premier projet de la liste)
            int formateurId = projets.get(0).getFormateur_id();

            // Créer le nouveau projet avec le formateur_id
            Projet mergedProject = new Projet(categorieId, mergedTitle, mergedDescription, mergedContenu, formateurId);
            projetService.ajouter(mergedProject);

            // Supprimer les anciens projets
            for (Projet projet : projets) {
                projetService.supprimer(projet);
            }

            System.out.println("Merged " + projets.size() + " projects into: " + mergedTitle);
        } catch (Exception e) {
            System.err.println("Failed to merge projects for category ID: " + categorieId);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FusionServer server = new FusionServer();
        server.start();
    }


}
