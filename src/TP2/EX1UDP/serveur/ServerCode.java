package TP2.EX1UDP.serveur;


import TP2.EX1UDP.interfaces.MathOpsImpl;

import java.io.*;
import java.net.*;

public class ServerCode {

    public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(5000);
        System.out.println("Serveur UDP prêt, attente des clients...");

        byte[] receiveData = new byte[1024];
        byte[] sendData;

        MathOpsImpl ops = new MathOpsImpl();

        while (true) {
            // Recevoir le premier message du client
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Client connecté depuis " + clientAddress + ":" + clientPort);

            // Envoyer le message de bienvenue
            sendMessage(serverSocket, clientAddress, clientPort, "Connexion réussie. Je vous écoute clairement.");

            boolean continuer = true;

            while (continuer) {
                // Envoyer le menu
                String menu = "=== MENU ===\n" +
                        "1 - Factorielle\n" +
                        "2 - Puissance\n" +
                        "3 - Racine carrée\n" +
                        "4 - Équation du second degré\n" +
                        "0 - Quitter\n" +
                        "END_MENU";
                sendMessage(serverSocket, clientAddress, clientPort, menu);

                // Recevoir le choix
                String choixStr = receiveMessage(serverSocket, receiveData);
                int choix = Integer.parseInt(choixStr);

                // Si choix = 0, on quitte
                if (choix == 0) {
                    sendMessage(serverSocket, clientAddress, clientPort, "Au revoir !");
                    continuer = false;
                    continue;
                }

                String result = "";

                switch (choix) {
                    case 1 -> {
                        sendMessage(serverSocket, clientAddress, clientPort, "Entrer n : ");
                        int n = Integer.parseInt(receiveMessage(serverSocket, receiveData));
                        result = "Factorielle = " + ops.factorielle(n);
                    }
                    case 2 -> {
                        sendMessage(serverSocket, clientAddress, clientPort, "Base : ");
                        int base = Integer.parseInt(receiveMessage(serverSocket, receiveData));
                        sendMessage(serverSocket, clientAddress, clientPort, "Exposant : ");
                        int exp = Integer.parseInt(receiveMessage(serverSocket, receiveData));
                        result = "Puissance = " + ops.puissance(base, exp);
                    }
                    case 3 -> {
                        sendMessage(serverSocket, clientAddress, clientPort, "Entrer x : ");
                        int x = Integer.parseInt(receiveMessage(serverSocket, receiveData));
                        result = "Racine carrée = " + ops.racineCarree(x);
                    }
                    case 4 -> {
                        sendMessage(serverSocket, clientAddress, clientPort, "Entrer a : ");
                        double a = Double.parseDouble(receiveMessage(serverSocket, receiveData));
                        sendMessage(serverSocket, clientAddress, clientPort, "Entrer b : ");
                        double b = Double.parseDouble(receiveMessage(serverSocket, receiveData));
                        sendMessage(serverSocket, clientAddress, clientPort, "Entrer c : ");
                        double c = Double.parseDouble(receiveMessage(serverSocket, receiveData));
                        result = ops.equationSecondDegre(a, b, c);
                    }
                    default -> result = "Choix invalide.";
                }

                // Envoyer le résultat
                sendMessage(serverSocket, clientAddress, clientPort, result);

                // Demander si on continue
                sendMessage(serverSocket, clientAddress, clientPort, "CONTINUE_QUESTION");
                String reponse = receiveMessage(serverSocket, receiveData);
                if (reponse.equalsIgnoreCase("non") || reponse.equalsIgnoreCase("n")) {
                    continuer = false;
                    sendMessage(serverSocket, clientAddress, clientPort, "Merci d'avoir utilisé le service. Au revoir !");
                }
            }

            System.out.println("Client déconnecté : " + clientAddress + ":" + clientPort);
        }
    }

    // Méthode pour envoyer un message UDP
    private static void sendMessage(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        socket.send(sendPacket);
    }

    // Méthode pour recevoir un message UDP
    private static String receiveMessage(DatagramSocket socket, byte[] receiveData) throws IOException {
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }
}