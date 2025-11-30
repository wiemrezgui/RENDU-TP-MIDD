package TP1.test;

import TP1.iterators.Iterator;
import TP1.models.Bibliotheque;
import TP1.models.Livre;

import java.util.*;


public class TestBibliotheque {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Saisie du nom de la bibliothèque
        System.out.print("Entrez le nom de la bibliothèque : ");
        String bibName = scanner.nextLine();
        Bibliotheque bib = new Bibliotheque(bibName);

        System.out.println("\n=== Création de la bibliothèque : " + bibName + " ===");

        // Ajout des livres
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- Ajout d'un nouveau livre ---");

            // Saisie du titre
            System.out.print("Titre du livre : ");
            String titre = scanner.nextLine().trim();

            // Vérifier si le livre existe déjà
            if (livreExisteDeja(bib, titre)) {
                System.out.println("Ce livre existe déjà dans la bibliothèque !");
                    continue;
            }

            // Saisie des thèmes
            List<String> themes = saisirThemes(scanner);

            // Création et ajout du livre
            Livre livre = new Livre(titre, themes);
            bib.ajouterLivre(livre);

            System.out.println(" Livre '" + titre + "' ajouté avec succès !");

            // Demander si on continue
            System.out.print("\nVoulez-vous ajouter un autre livre ? (o/n) : ");
            String reponse = scanner.nextLine().toLowerCase();
            continuer = reponse.equals("o") || reponse.equals("oui");
        }

        // Tests des recherches par thème
        System.out.println("\n=== RECHERCHES PAR THÈME ===");

        boolean rechercher = true;
        while (rechercher) {
            System.out.print("\nEntrez un thème à rechercher (ou 'quit' pour quitter) : ");
            String themeRecherche = scanner.nextLine().trim();

            if (themeRecherche.equalsIgnoreCase("quit")) {
                break;
            }

            if (themeRecherche.isEmpty()) {
                System.out.println(" Veuillez entrer un thème valide");
                continue;
            }

            System.out.println("\nLivres du thème '" + themeRecherche + "' :");
            Iterator<Livre> it = bib.iteratorTheme(themeRecherche);

            boolean found = false;
            int compteur = 0;
            while (it.hasNext()) {
                System.out.println("• " + it.next());
                found = true;
                compteur++;
            }

            if (!found) {
                System.out.println("Aucun livre trouvé pour le thème '" + themeRecherche + "'");
            } else {
                System.out.println("→ " + compteur + " livre(s) trouvé(s)");
            }

            // Afficher tous les thèmes disponibles
            System.out.println("\nThèmes disponibles dans la bibliothèque :");
            Set<String> tousThemes = getTousThemes(bib);
            if (tousThemes.isEmpty()) {
                System.out.println("Aucun thème disponible");
            } else {
                for (String theme : tousThemes) {
                    System.out.println("- " + theme);
                }
            }
        }

        // Affichage final de tous les livres
        System.out.println("\n=== RÉCAPITULATIF DE LA BIBLIOTHÈQUE ===");
        System.out.println("Nombre total de livres : " + bib.getLivres().size());
        for (Livre livre : bib.getLivres()) {
            System.out.println("• " + livre);
        }

        System.out.println("\n=== FIN DU PROGRAMME ===");
        scanner.close();
    }

    // Méthode pour vérifier si un livre existe déjà
    private static boolean livreExisteDeja(Bibliotheque biblio, String titre) {
        for (Livre livre : biblio.getLivres()) {
            if (livre.getTitre().equalsIgnoreCase(titre)) {
                return true;
            }
        }
        return false;
    }

    // Méthode pour saisir les thèmes d'un livre
    private static List<String> saisirThemes(Scanner scanner) {
        List<String> themes = new ArrayList<>();

        System.out.println("Saisie des thèmes (un par ligne, ligne vide pour terminer) :");

        boolean saisieTheme = true;
        while (saisieTheme) {
            System.out.print("Thème (ou Enter pour terminer) : ");
            String theme = scanner.nextLine().trim();

            if (theme.isEmpty()) {
                if (themes.isEmpty()) {
                    System.out.println("Veuillez saisir au moins un thème !");
                    continue;
                }
                saisieTheme = false;
            } else {
                // Normaliser le thème (première lettre majuscule, reste minuscule)
                theme = normaliserTheme(theme);

                // Éviter les doublons
                if (!themeDejaPresent(themes, theme)) {
                    themes.add(theme);
                    System.out.println(" Thème ajouté : " + theme);
                } else {
                    System.out.println(" Ce thème a déjà été ajouté");
                }
            }
        }

        return themes;
    }

    // Méthode pour normaliser un thème (première lettre majuscule)
    private static String normaliserTheme(String theme) {
        if (theme == null || theme.isEmpty()) {
            return theme;
        }
        return theme.substring(0, 1).toUpperCase() + theme.substring(1).toLowerCase();
    }

    // Méthode pour vérifier si un thème existe déjà
    private static boolean themeDejaPresent(List<String> themes, String nouveauTheme) {
        for (String theme : themes) {
            if (theme.equalsIgnoreCase(nouveauTheme)) {
                return true;
            }
        }
        return false;
    }

    // Méthode pour récupérer tous les thèmes uniques de la bibliothèque
    private static Set<String> getTousThemes(Bibliotheque biblio) {
        Set<String> tousThemes = new TreeSet<>(); // TreeSet pour tri automatique

        for (Livre livre : biblio.getLivres()) {
            for (String theme : livre.getThemes()) {
                tousThemes.add(theme);
            }
        }

        return tousThemes;
    }
}