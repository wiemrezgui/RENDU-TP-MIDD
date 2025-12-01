package TP2.EX2UDP.server;

import TP2.EX2UDP.services.ServiceImpl;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {

    private DatagramSocket serverSocket;
    private InetAddress clientAddress;
    private int clientPort;

    public ClientHandler(DatagramSocket serverSocket, InetAddress clientAddress, int clientPort) {
        this.serverSocket = serverSocket;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    @Override
    public void run() {
        String clientInfo = clientAddress + ":" + clientPort;
        System.out.println("Début du traitement pour le client: " + clientInfo);

        try {
            ServiceImpl serviceImpl = new ServiceImpl();
            boolean continuer = true;

            // Message de bienvenue
            sendMessage("=== BIENVENUE DANS LE SYSTÈME BANCAIRE ===");
            sendMessage("Services disponibles :");
            sendMessage("1 - Bonus (+25% du salaire brut)");
            sendMessage("2 - Insurance (-5% du salaire brut)");
            sendMessage("3 - Tax (-15% du salaire brut)");
            sendMessage("4 - Salaire Net (application de tous les traitements)");
            sendMessage("0 - Quitter");

            while (continuer) {
                // Demander explicitement le salaire
                sendMessage(">>> Veuillez saisir votre salaire brut (ou 0 pour quitter) : ");
                String saisieSalaire = receiveMessage();

                // Vérifier si l'utilisateur veut quitter
                if (saisieSalaire == null || saisieSalaire.equals("0")) {
                    sendMessage("Au revoir !");
                    continuer = false;
                    continue;
                }

                try {
                    double salaire = Double.parseDouble(saisieSalaire);
                    if (salaire < 0) {
                        sendMessage("Erreur : Le salaire ne peut pas être négatif.");
                        continue;
                    }

                    // Demander explicitement le service
                    sendMessage(">>> Choisissez un service (1-4) ou 0 pour quitter : ");
                    String option = receiveMessage();

                    if (option == null || option.equals("0")) {
                        sendMessage("Au revoir !");
                        continuer = false;
                        continue;
                    }

                    double resultat = 0;
                    String message = "";

                    switch (option) {
                        case "1":
                            resultat = serviceImpl.traiterBonus(salaire);
                            message = String.format("Salaire après Bonus: %.2f → %.2f (+25%%)", salaire, resultat);
                            break;
                        case "2":
                            resultat = serviceImpl.traiterInsurance(salaire);
                            message = String.format("Salaire après Insurance: %.2f → %.2f (-5%%)", salaire, resultat);
                            break;
                        case "3":
                            resultat = serviceImpl.traiterTax(salaire);
                            message = String.format("Salaire après Tax: %.2f → %.2f (-15%%)", salaire, resultat);
                            break;
                        case "4":
                            resultat = serviceImpl.traiterSalaireNet(salaire);
                            message = String.format("Salaire Net: %.2f → %.2f (Bonus+Insurance+Tax)", salaire, resultat);
                            break;
                        default:
                            sendMessage("Option invalide. Veuillez choisir entre 1 et 4.");
                            continue;
                    }

                    sendMessage("RÉSULTAT: " + message);

                    // Demander si le client veut continuer
                    sendMessage(">>> Voulez-vous effectuer une autre opération ? (oui/non)");
                    String reponse = receiveMessage();
                    if (reponse != null && (reponse.equalsIgnoreCase("non") || reponse.equalsIgnoreCase("n"))) {
                        sendMessage("Merci d'avoir utilisé nos services. Au revoir !");
                        continuer = false;
                    }

                } catch (NumberFormatException e) {
                    sendMessage("Erreur : Veuillez entrer un nombre valide pour le salaire.");
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur avec le client " + clientInfo + ": " + e.getMessage());
        } finally {
            System.out.println("Client déconnecté: " + clientInfo);
        }
    }

    private void sendMessage(String message) throws IOException {
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        serverSocket.send(sendPacket);
    }

    private String receiveMessage() throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
    }
}