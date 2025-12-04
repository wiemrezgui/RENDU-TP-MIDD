package TP4.GestionEtudiants;

import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

/**
 * Classe d'implémentation de l'interface Promotion
 * Gère une collection d'étudiants
 */
public class PromotionImpl extends PromotionPOA {

    // Map pour stocker les étudiants: clé = numéro étudiant, valeur = objet Etudiant
    private Map<Long, EtudiantImpl> etudiants;

    // Référence à l'ORB pour créer des références d'objets
    private ORB orb;

    // Référence au POA (Portable Object Adapter)
    private POA poa;

    public PromotionImpl(ORB orb, POA poa) {
        this.etudiants = new HashMap<>();
        this.orb = orb;
        this.poa = poa;
    }

    //Ajouter un nouvel étudiant à la promotion
    @Override
    public Etudiant ajouter_un_etudiant(String nom, String prenom, long numeroEtudiant) {
        try {
            // Vérifier si l'étudiant existe déjà
            if (etudiants.containsKey(numeroEtudiant)) {
                System.out.println("ATTENTION: Un étudiant avec ce numéro existe déjà!");
                // Retourner la référence existante
                EtudiantImpl etudiantExistant = etudiants.get(numeroEtudiant);
                return etudiantExistant._this(orb);
            }

            // Créer une nouvelle instance d'implémentation d'étudiant
            EtudiantImpl nouvelEtudiant = new EtudiantImpl(nom, prenom, numeroEtudiant);

            // Activer l'objet avec le POA pour obtenir une référence CORBA
            byte[] id = poa.activate_object(nouvelEtudiant);

            // Obtenir la référence CORBA de l'objet
            org.omg.CORBA.Object obj = poa.id_to_reference(id);
            Etudiant etudiantRef = EtudiantHelper.narrow(obj);

            // Stocker l'étudiant dans la map
            etudiants.put(numeroEtudiant, nouvelEtudiant);

            System.out.println("Étudiant ajouté: " + nom + " " + prenom +
                    " (Numéro: " + numeroEtudiant + ")");

            return etudiantRef;

        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout d'un étudiant: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Rechercher un étudiant par son numéro
    @Override
    public Etudiant rechercher_un_etudiant(long numeroEtudiant) {
        try {
            // Vérifier si l'étudiant existe dans la map
            if (!etudiants.containsKey(numeroEtudiant)) {
                System.out.println("Étudiant non trouvé avec le numéro: " + numeroEtudiant);
                return null;
            }

            // Récupérer l'implémentation de l'étudiant
            EtudiantImpl etudiantImpl = etudiants.get(numeroEtudiant);

            // Retourner la référence CORBA
            return etudiantImpl._this(orb);

        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche d'un étudiant: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Calculer la moyenne générale de toute la promotion
    @Override
    public double calculer_moyenne_de_la_promotion() {
        // Si aucun étudiant, retourner 0
        if (etudiants.isEmpty()) {
            System.out.println("Aucun étudiant dans la promotion.");
            return 0.0;
        }

        double sommeMoyennes = 0.0;
        int nombreEtudiants = 0;

        // Parcourir tous les étudiants
        for (EtudiantImpl etudiant : etudiants.values()) {
            double moyenneEtudiant = etudiant.calculer_la_moyenne();

            // Ne comptabiliser que les étudiants qui ont des épreuves
            if (moyenneEtudiant > 0.0) {
                sommeMoyennes += moyenneEtudiant;
                nombreEtudiants++;
            }
        }

        // Calculer et retourner la moyenne de la promotion
        if (nombreEtudiants == 0) {
            return 0.0;
        }

        double moyennePromotion = sommeMoyennes / nombreEtudiants;

        System.out.println("Moyenne de la promotion: " +
                String.format("%.2f", moyennePromotion) +
                " (sur " + nombreEtudiants + " étudiants)");

        return moyennePromotion;
    }

    public void afficherTousLesEtudiants() {
        System.out.println("\n=== Liste des étudiants de la promotion ===");
        System.out.println("Nombre total d'étudiants: " + etudiants.size());

        for (EtudiantImpl etudiant : etudiants.values()) {
            etudiant.afficherInfos();
        }
    }
}
