package BINGO.gateway;

import BINGO.gateway.handlers.ClientHandler;
import BINGO.utils.ResultatTirage;
import BINGO.services.IBingoService;

import java.io.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Gateway {
    private static final int PORT = 5000;
    private IBingoService bingoService;
    private int meilleurScoreGlobal = 0;

    public Gateway() {
        try {
            // Connexion au serveur RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            bingoService = (IBingoService) registry.lookup("BingoService");
            System.out.println("Gateway connecté au serveur RMI");
        } catch (Exception e) {
            System.err.println("Erreur de connexion RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void demarrer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Gateway démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté: " + clientSocket.getInetAddress());

                // Créer un nouveau ClientHandler avec service RMI et score global
                Thread clientThread = new Thread(new ClientHandler(clientSocket, bingoService, meilleurScoreGlobal));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Erreur Gateway: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        Gateway gateway = new Gateway();
        gateway.demarrer();
    }
}
