package BINGO;

import java.io.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

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
            System.out.println("===================================");
            System.out.println("Gateway démarré sur le port " + PORT);
            System.out.println("En attente de clients...");
            System.out.println("===================================");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\nNouveau client connecté: " + clientSocket.getInetAddress());

                // Créer un thread pour gérer chaque client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Erreur du serveur Gateway: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Classe interne pour gérer chaque client
    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Requête reçue: " + request);

                    String response = traiterRequete(request);
                    out.println(response);
                }

            } catch (IOException e) {
                System.out.println("Client déconnecté");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String traiterRequete(String request) {
            try {
                String[] parts = request.split(":");
                String commande = parts[0];

                switch (commande) {
                    case "JOUER":
                        return traiterJeu(parts[1]);

                    case "MEILLEUR_SCORE":
                        return "SCORE:" + meilleurScoreGlobal;

                    case "QUITTER":
                        return "Au revoir!";

                    default:
                        return "ERREUR:Commande inconnue";
                }
            } catch (Exception e) {
                return "ERREUR:" + e.getMessage();
            }
        }

        private String traiterJeu(String predictionStr) {
            try {
                // Parse la prédiction
                String[] nums = predictionStr.split(",");
                int[] prediction = new int[10];
                for (int i = 0; i < 10; i++) {
                    prediction[i] = Integer.parseInt(nums[i].trim());
                }

                // Appel RMI au serveur
                ResultatTirage resultat = bingoService.verifierPrediction(prediction);

                // Mise à jour du meilleur score
                if (resultat.getScore() > meilleurScoreGlobal) {
                    meilleurScoreGlobal = resultat.getScore();
                    bingoService.enregistrerScore(meilleurScoreGlobal);
                }

                // Format de réponse: RESULTAT:score,boule1,boule2,...
                StringBuilder response = new StringBuilder("RESULTAT:");
                response.append(resultat.getScore()).append(",");

                int[] boules = resultat.getBoulesTirees();
                for (int i = 0; i < boules.length; i++) {
                    response.append(boules[i]);
                    if (i < boules.length - 1) {
                        response.append(",");
                    }
                }

                return response.toString();

            } catch (Exception e) {
                return "ERREUR:" + e.getMessage();
            }
        }
    }

    public static void main(String[] args) {
        Gateway gateway = new Gateway();
        gateway.demarrer();
    }
}
