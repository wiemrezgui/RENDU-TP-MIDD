package TP4.GestionEtudiants;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

//Client CORBA pour gérer les épreuves des étudiants
public class ClientEpreuves {

    public static void main(String[] args) {
        try {
            // === ÉTAPE 1: Initialisation de l'ORB ===
            System.out.println("=== Client CORBA - Gestion des Épreuves ===\n");

            ORB orb = ORB.init(args, null);
            System.out.println("[1] ORB initialisé");


            // === ÉTAPE 2: Récupération du service de noms ===
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            System.out.println("[2] Service de noms contacté");


            // === ÉTAPE 3: Récupération de la référence de l'objet Promotion ===
            String name = "Promotion";
            Promotion promotion = PromotionHelper.narrow(ncRef.resolve_str(name));
            System.out.println("[3] Référence Promotion récupérée\n");


            // === ÉTAPE 4: Création et ajout d'étudiants ===
            System.out.println("=== Ajout des étudiants ===\n");

            // Créer le premier étudiant
            Etudiant etudiant1 = promotion.ajouter_un_etudiant("Wiem", "Rezgui", 12345);
            System.out.println("✓ Étudiant 1 créé");

            // Créer le deuxième étudiant
            Etudiant etudiant2 = promotion.ajouter_un_etudiant("Zeineb", "Zarrouk", 12346);
            System.out.println(" Étudiant 2 créé");

            // Créer le troisième étudiant
            Etudiant etudiant3 = promotion.ajouter_un_etudiant("Eya", "Sari", 12347);
            System.out.println(" Étudiant 3 créé)\n");


            // === ÉTAPE 5: Ajout d'épreuves pour l'étudiant 1 ===

            etudiant1.ajouter_une_epreuve("Mathématiques - Écrit", 15.5, 3.0);
            etudiant1.ajouter_une_epreuve("Mathématiques - Oral", 14.0, 2.0);
            etudiant1.ajouter_une_epreuve("Physique - Écrit", 16.0, 3.0);
            etudiant1.ajouter_une_epreuve("Informatique - TP", 17.5, 2.5);
            etudiant1.ajouter_une_epreuve("Anglais", 13.0, 1.5);


            // === ÉTAPE 6: Ajout d'épreuves pour l'étudiant 2 ===

            etudiant2.ajouter_une_epreuve("Mathématiques - Écrit", 18.0, 3.0);
            etudiant2.ajouter_une_epreuve("Mathématiques - Oral", 17.5, 2.0);
            etudiant2.ajouter_une_epreuve("Physique - Écrit", 16.5, 3.0);
            etudiant2.ajouter_une_epreuve("Informatique - TP", 19.0, 2.5);
            etudiant2.ajouter_une_epreuve("Anglais", 15.5, 1.5);


            // === ÉTAPE 7: Ajout d'épreuves pour l'étudiant 3 ===

            etudiant3.ajouter_une_epreuve("Mathématiques - Écrit", 12.0, 3.0);
            etudiant3.ajouter_une_epreuve("Mathématiques - Oral", 11.5, 2.0);
            etudiant3.ajouter_une_epreuve("Physique - Écrit", 13.5, 3.0);
            etudiant3.ajouter_une_epreuve("Informatique - TP", 14.0, 2.5);
            etudiant3.ajouter_une_epreuve("Anglais", 12.5, 1.5);


            // === ÉTAPE 8: Affichage des résultats ===
            System.out.println("\n\n" + "=".repeat(60));
            System.out.println("           RÉSULTATS DE LA PROMOTION");
            System.out.println("=".repeat(60) + "\n");

            // Afficher les détails de chaque étudiant
            afficherDetailsEtudiant(etudiant1);
            afficherDetailsEtudiant(etudiant2);
            afficherDetailsEtudiant(etudiant3);


            // === ÉTAPE 9: Calcul de la moyenne de la promotion ===
            System.out.println("\n" + "=".repeat(60));
            double moyennePromotion = promotion.calculer_moyenne_de_la_promotion();
            System.out.println("MOYENNE GÉNÉRALE DE LA PROMOTION: " +
                    String.format("%.2f", moyennePromotion) + "/20");
            System.out.println("=".repeat(60) + "\n");



        } catch (Exception e) {
            System.err.println("ERREUR dans le client: " + e.getMessage());
            e.printStackTrace();
        }
    }

}