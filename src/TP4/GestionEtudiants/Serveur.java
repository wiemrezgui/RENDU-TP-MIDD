package TP4.GestionEtudiants;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

//Classe principale du serveur CORBA
// Initialise l'ORB, crée l'objet Promotion et l'enregistre dans le service de noms
public class Serveur {

    public static void main(String[] args) {
        try {
            // === ÉTAPE 1: Initialisation de l'ORB ===
            System.out.println("=== Démarrage du Serveur CORBA ===\n");

            // Créer et initialiser l'ORB (Object Request Broker)
            // L'ORB est le middleware qui gère la communication CORBA
            ORB orb = ORB.init(args, null);
            System.out.println("[1] ORB initialisé avec succès");


            // === ÉTAPE 2: Obtention du POA (Portable Object Adapter) ===
            // Le POA gère le cycle de vie des objets serveurs
            org.omg.CORBA.Object poaObj = orb.resolve_initial_references("RootPOA");
            POA rootPOA = POAHelper.narrow(poaObj);
            System.out.println("[2] POA récupéré");

            // Activer le POA Manager
            // Le POA Manager contrôle l'état du POA (actif, inactif, etc.)
            rootPOA.the_POAManager().activate();
            System.out.println("[3] POA Manager activé");


            // === ÉTAPE 3: Création de l'objet servant (implémentation) ===
            // Créer une instance de PromotionImpl
            PromotionImpl promotionServant = new PromotionImpl(orb, rootPOA);
            System.out.println("[4] Objet Promotion créé");


            // === ÉTAPE 4: Activation de l'objet avec le POA ===
            // Activer l'objet pour qu'il puisse recevoir des requêtes
            byte[] id = rootPOA.activate_object(promotionServant);
            System.out.println("[5] Objet Promotion activé dans le POA");


            // === ÉTAPE 5: Obtention de la référence CORBA ===
            // Convertir l'objet servant en référence CORBA
            org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);
            Promotion promotionRef = PromotionHelper.narrow(ref);
            System.out.println("[6] Référence CORBA obtenue");


            // === ÉTAPE 6: Enregistrement dans le service de noms ===
            // Le service de noms permet aux clients de trouver les objets par leur nom
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            System.out.println("[7] Service de noms contacté");

            // Créer un nom pour l'objet dans le service de noms
            String name = "Promotion";
            NameComponent[] path = ncRef.to_name(name);

            // Enregistrer la référence de l'objet dans le service de noms
            ncRef.rebind(path, promotionRef);
            System.out.println("[8] Objet Promotion enregistré dans le service de noms sous: '" + name + "'");


            // === ÉTAPE 7: Mise en attente des requêtes ===
            System.out.println("Serveur prêt et en attente de requêtes...\n");
            System.out.println("Pour arrêter le serveur, appuyez sur Ctrl+C\n");

            // Boucle principale du serveur
            // orb.run() bloque le thread et attend les requêtes des clients
            orb.run();


        } catch (Exception e) {
            // Gestion des erreurs
            System.err.println("ERREUR dans le serveur: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nServeur arrêté.");
    }
}