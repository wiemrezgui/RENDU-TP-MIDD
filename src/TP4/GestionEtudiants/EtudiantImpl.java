package TP4.GestionEtudiants;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'implémentation de l'interface Etudiant
 * Hérite de EtudiantPOA (généré par idlj)
 */
public class EtudiantImpl extends EtudiantPOA {

    // Attributs privés de l'étudiant
    private String nom;
    private String prenom;
    private long numeroEtudiant;

    // Liste des épreuves passées par l'étudiant
    private List<Epreuve> epreuves;

    // Liste des livres empruntés par l'étudiant
    private List<Livre> livres;

    public EtudiantImpl(String nom, String prenom, long numeroEtudiant) {
        this.nom = nom;
        this.prenom = prenom;
        this.numeroEtudiant = numeroEtudiant;
        this.epreuves = new ArrayList<>();
        this.livres = new ArrayList<>();
    }

    @Override
    public String nom() {
        return this.nom;
    }
    @Override
    public String prenom() {
        return this.prenom;
    }

    @Override
    public long numeroEtudiant() {
        return this.numeroEtudiant;
    }

    //Méthode 1: Ajouter une épreuve à l'étudiant
    @Override
    public void ajouter_une_epreuve(String nomEpreuve, double note, double coefficient) {
        // Créer une nouvelle épreuve avec les paramètres fournis
        Epreuve nouvelleEpreuve = new Epreuve(nomEpreuve, note, coefficient);

        // Ajouter l'épreuve à la liste
        epreuves.add(nouvelleEpreuve);

        System.out.println("Épreuve ajoutée: " + nomEpreuve +
                " - Note: " + note +
                " - Coefficient: " + coefficient);
    }

    // Méthode 2: Récupérer la liste des noms des épreuves
     // return Un tableau de chaînes contenant les noms des épreuves
    @Override
    public String[] liste_des_epreuves() {
        // Créer un tableau de la taille de la liste d'épreuves
        String[] nomsEpreuves = new String[epreuves.size()];

        // Parcourir la liste et extraire les noms
        for (int i = 0; i < epreuves.size(); i++) {
            nomsEpreuves[i] = epreuves.get(i).nom;
        }

        return nomsEpreuves;
    }

    // Méthode 3: Calculer la moyenne générale de l'étudiant
     // Calcul: (somme des (note * coefficient)) / (somme des coefficients)
    @Override
    public double calculer_la_moyenne() {
        // Si aucune épreuve, retourner 0
        if (epreuves.isEmpty()) {
            return 0.0;
        }

        double sommeNotesPonderees = 0.0;  // Somme des (note * coefficient)
        double sommeCoefficients = 0.0;     // Somme des coefficients

        // Parcourir toutes les épreuves
        for (Epreuve epreuve : epreuves) {
            sommeNotesPonderees += epreuve.note * epreuve.coefficient;
            sommeCoefficients += epreuve.coefficient;
        }

        // Calculer et retourner la moyenne pondérée
        // Éviter la division par zéro
        if (sommeCoefficients == 0.0) {
            return 0.0;
        }

        return sommeNotesPonderees / sommeCoefficients;
    }

    // Méthode 4: Emprunter un livre
    @Override
    public void emprunter_un_livre(long numero, String nom, String auteur,
                                   String collection, String datePublication) {
        // Vérifier que l'étudiant n'a pas déjà emprunté 2 livres
        if (livres.size() >= 2) {
            System.out.println("ATTENTION: L'étudiant a déjà emprunté 2 livres (maximum atteint)!");
            return;
        }

        // Créer un nouveau livre
        Livre nouveauLivre = new Livre(numero, nom, auteur, collection, datePublication);

        // Ajouter le livre à la liste
        livres.add(nouveauLivre);

        System.out.println("Livre emprunté: " + nom +
                " par " + auteur +
                " (Numéro: " + numero + ")");
    }

    //Méthode 5 : Obtenir la liste des livres empruntés
    @Override
    public Livre[] obtenir_livres_empruntes() {
        // Convertir la liste en tableau
        return livres.toArray(new Livre[0]);
    }

    //Méthode utilitaire pour afficher les informations de l'étudiant
    public void afficherInfos() {
        System.out.println("\n=== Informations Étudiant ===");
        System.out.println("Nom: " + nom);
        System.out.println("Prénom: " + prenom);
        System.out.println("Numéro: " + numeroEtudiant);
        System.out.println("Nombre d'épreuves: " + epreuves.size());
        System.out.println("Moyenne générale: " + String.format("%.2f", calculer_la_moyenne()));
        System.out.println("Livres empruntés: " + livres.size());
    }
}