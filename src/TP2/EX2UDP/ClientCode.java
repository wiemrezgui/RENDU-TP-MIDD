package TP2.EX2UDP;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientCode {

    public static void main(String[] args) {
        try (
                DatagramSocket clientSocket = new DatagramSocket();
                Scanner sc = new Scanner(System.in);
        ) {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 5000;

            System.out.println("Connexion au serveur UDP établie...");

            // Envoyer un premier message pour initier la communication
            sendMessage(clientSocket, serverAddress, serverPort, "START");

            byte[] receiveData = new byte[1024];
            String serverMsg;

            while (true) {
                // Recevoir le message du serveur
                serverMsg = receiveMessage(clientSocket, receiveData);

                // Afficher tout ce que le serveur envoie
                System.out.println(serverMsg);

                // NE répondre que si une ligne contient ">>>"
                if (serverMsg.trim().startsWith(">>>")) {
                    String userInput = sc.nextLine();
                    sendMessage(clientSocket, serverAddress, serverPort, userInput);

                    if (userInput.equals("0")) {
                        break;
                    }
                }

                // Condition de sortie
                if (serverMsg.contains("Au revoir") || serverMsg.contains("Merci")) {
                    break;
                }
            }

            System.out.println("Déconnexion du serveur.");

        } catch (Exception e) {
            System.out.println("Erreur client : " + e.getMessage());
        }
    }

    private static void sendMessage(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        socket.send(sendPacket);
    }

    private static String receiveMessage(DatagramSocket socket, byte[] receiveData) throws IOException {
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
    }
}