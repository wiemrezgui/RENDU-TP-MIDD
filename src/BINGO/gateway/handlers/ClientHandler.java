package BINGO.gateway.handlers;

import BINGO.utils.ResultatTirage;
import BINGO.services.IBingoService;

import java.io.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe dédiée à la gestion d'un client individuel
 * Traite toutes les communications avec un client spécifique
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private IBingoService bingoService;
    private int meilleurScoreGlobal;

    public ClientHandler(Socket socket, IBingoService bingoService, int meilleurScoreGlobal) {
        this.socket = socket;
        this.bingoService = bingoService;
        this.meilleurScoreGlobal = meilleurScoreGlobal;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Nouvelle session client démarrée");

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Requête reçue: " + request);
                String response = traiterRequete(request);
                out.println(response);
            }

        } catch (IOException e) {
            System.out.println("Client déconnecté: " + e.getMessage());
        } finally {
            fermerConnexion();
        }
    }

    private String traiterRequete(String request) {
        try {
            if (request == null || request.isEmpty()) {
                return "ERREUR:Requête vide";
            }

            String[] parts = request.split(":", 2);
            String commande = parts[0];

            switch (commande) {
                case "JOUER":
                    return parts.length > 1 ? traiterJeu(parts[1]) : "ERREUR:Prédiction manquante";

                case "MEILLEUR_SCORE":
                    return "SCORE:" + meilleurScoreGlobal;

                case "QUITTER":
                    return "AU_REVOIR:Session terminée";

                default:
                    return "ERREUR:Commande '" + commande + "' non reconnue";
            }
        } catch (Exception e) {
            return "ERREUR:" + e.getMessage();
        }
    }

    private String traiterJeu(String predictionStr) {
        try {
            // Validation et parsing
            String[] nums = predictionStr.split(",");
            if (nums.length != 10) {
                return "ERREUR:10 numéros requis";
            }

            int[] prediction = new int[10];
            Set<Integer> dejaVus = new HashSet<>();

            for (int i = 0; i < 10; i++) {
                try {
                    int num = Integer.parseInt(nums[i].trim());
                    if (num < 0 || num > 9) {
                        return "ERREUR:Numéro " + num + " hors limites (0-9)";
                    }
                    if (!dejaVus.add(num)) {
                        return "ERREUR:Numéro " + num + " dupliqué";
                    }
                    prediction[i] = num;
                } catch (NumberFormatException e) {
                    return "ERREUR:Valeur non numérique: " + nums[i];
                }
            }

            // Appel RMI au serveur
            ResultatTirage resultat = bingoService.verifierPrediction(prediction);

            // Mise à jour du meilleur score
            if (resultat.getScore() > meilleurScoreGlobal) {
                meilleurScoreGlobal = resultat.getScore();
                bingoService.enregistrerScore(meilleurScoreGlobal);
            }

            // Construction de la réponse
            StringBuilder response = new StringBuilder("RESULTAT:");
            response.append(resultat.getScore()).append(",");

            int[] boules = resultat.getBoulesTirees();
            for (int i = 0; i < boules.length; i++) {
                response.append(boules[i]);
                if (i < boules.length - 1) response.append(",");
            }

            return response.toString();

        } catch (Exception e) {
            return "ERREUR:" + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    private void fermerConnexion() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture des ressources: " + e.getMessage());
        }
    }
}