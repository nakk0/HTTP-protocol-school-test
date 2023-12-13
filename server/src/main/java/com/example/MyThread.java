package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MyThread extends Thread {
    private Socket client;
    private ArrayList<MyThread> threads;
    private DataOutputStream out;
    private String clientName;

    public MyThread(Socket socket, ArrayList<MyThread> threads) {
        this.client = socket;
        this.threads = threads;
    }

    public void run() {
        try {
            BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());

            System.out.println("Client connected");

            clientName = in.readLine();
            System.out.println("Received name: " + clientName);

            String choice;
            do {
                choice = in.readLine();

                if (choice.equals("0")) {
                    String message = in.readLine();
                    broadcastMessage(clientName + ": " + message);
                } else if (choice.equals("1")) {
                    String recipient = in.readLine();
                    String message = in.readLine();

                    sendPrivateMessage(clientName, recipient, message);
                }
            } while (!choice.equals("2"));

            client.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(String message, DataOutputStream out) {
        try {
            out.writeBytes(message + "\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void broadcastMessage(String message) {
        for (MyThread thread : threads) {
            if (thread != this) {
                thread.sendMessage(message, thread.out);
            }
        }
    }

    private void sendPrivateMessage(String sender, String recipient, String message) {
        for (MyThread thread : threads) {
            if (thread != this && thread.getClientName().equals(recipient)) {
                thread.sendMessage(sender + " (private): " + message, thread.out);
            }
        }
    }


    public String getClientName() {
        return clientName;
    }
}
