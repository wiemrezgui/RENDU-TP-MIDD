package TP4.GestionEtudiants;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

//Client CORBA pour gérer les emprunts de livres des étudiants
public class ClientLivres {

    public static void main(String[] args) {
        try {
            // === ÉTAPE 1: Initialisation de l'ORB ===
            System.out.println("=== Client CORBA - Gestion des Emprunts de Livres ===\n");

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
            System.out.println("=== Création des étudiants ===\n");

            Etudiant etudiant1 = promotion.ajouter_un_etudiant("Wiem", "Rezgui", 54321);
            System.out.println(" Etudiant 1 créé");

            Etudiant etudiant2 = promotion.ajouter_un_etudiant("Zeineb", "Zarrouk", 54322);
            System.out.println(" Étudiant 2 créé");

            Etudiant etudiant3 = promotion.ajouter_un_etudiant("Eya", "Sari", 54323);
            System.out.println(" Étudiant 3 créé\n");


            // === ÉTAPE 5: Emprunt de livres pour l'étudiant 1 ===
            System.out.println("=== Emprunts de Marie Rousseau ===\n");

            etudiant1.emprunter_un_livre(
                    1001,
                    "Introduction à Java",
                    "Herbert Schildt",
                    "Oracle Press",
                    "2022"
            );

            etudiant1.emprunter_un_livre(
                    1002,
                    "Design Patterns",
                    "Gang of Four",
                    "Addison-Wesley",
                    "1994"
            );

            // Test: tentative d'emprunter un 3ème livre (devrait échouer)
            System.out.println("\nTentative d'emprunter un 3ème livre (limite: 2):");
            etudiant1.emprunter_un_livre(
                    1003,
                    "Clean Code",
                    "Robert C. Martin",
                    "Prentice Hall",
                    "2008"
            );


            // === ÉTAPE 6: Emprunt de livres pour l'étudiant 2 ===
            System.out.println("\n=== Emprunts de Thomas Lefebvre ===\n");

            etudiant2.emprunter_un_livre(
                    2001,
                    "Programmation Réseau en Java",
                    "Elliotte Rusty Harold",
                    "O'Reilly",
                    "2013"
            );

            etudiant2.emprunter_un_livre(
                    2002,
                    "Systèmes Distribués",
                    "Andrew Tanenbaum",
                    "Pearson",
                    "2017"
            );


            // === ÉTAPE 7: Emprunt d'un seul livre pour l'étudiant 3 ===
            System.out.println("\n=== Emprunts d'Emma Moreau ===\n");

            etudiant3.emprunter_un_livre(
                    3001,
                    "Bases de Données",
                    "Ramez Elmasri",
                    "Addison-Wesley",
                    "2015"
            );


            // === ÉTAPE 8: Affichage des emprunts ===
            System.out.println("\n\n" + "=".repeat(70));
            System.out.println("              LISTE DES EMPRUNTS DE LIVRES");
            System.out.println("=".repeat(70) + "\n");

            afficherEmpruntsEtudiant(etudiant1);
            afficherEmpruntsEtudiant(etudiant2);
            afficherEmpruntsEtudiant(etudiant3);

        } catch (Exception e) {
            System.err.println("ERREUR dans le client: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
