package BINGO.server;

import BINGO.services.BingoServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServeurApplication {
    public static void main(String[] args) {
        try {
            // Création du service Bingo
            BingoServiceImpl bingoService = new BingoServiceImpl();

            // Création du registre RMI sur le port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // Enregistrement du service dans le registre
            registry.rebind("BingoService", bingoService);

            System.out.println("===================================");
            System.out.println("Serveur Bingo démarré avec succès!");
            System.out.println("En attente des requêtes...");
            System.out.println("===================================");

        } catch (Exception e) {
            System.err.println("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}