package com.example;

import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class App {
    public static void main(String[] args) {
        try {
            System.out.println("Server started and waiting for connections...");
            ServerSocket server = new ServerSocket(3000);
            System.out.println(System.getProperty("user.dir"));
            while (true) {
                Socket client = server.accept();

                BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(client.getInputStream()));
                DataOutputStream out = new DataOutputStream(client.getOutputStream());

                String message;
                message = in.readLine();
                String[] messages = message.split(" ");
                String path = messages[1].substring(1);

                while (!message.isEmpty()) {
                    message = in.readLine();
                    System.out.println(message);
                }

                File file = new File(path);

                if (file.exists()) {
                    System.out.println("the file exists");
                    sendBinaryFile(file, out);
                } else {
                    message = "file not found";
                    System.out.println(message);
                    out.writeBytes("HTTP/1.1 404 NOT FOUND\n");
                    out.writeBytes("Content-length: " + message.length() + "\n");
                    out.writeBytes("Content-type: text/plain\n");
                    out.writeBytes("\n");
                    out.writeBytes(message + "\n");
                }

                in.close();
                out.close();
                client.close();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void sendBinaryFile(File file, DataOutputStream out) {
        try {
            out.writeBytes("HTTP/1.1 200 OK\n");
            out.writeBytes("Content-length: " + file.length() + "\n");
            out.writeBytes("Content-type: " + getContentType(file) + "\n");
            out.writeBytes("\n");

            String binary = Files.readString(file.toPath());
            out.writeBytes(binary + "\n");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static String getContentType(File file) {
        String mime = "application/octet-stream";
        try {
            mime = Files.probeContentType(file.toPath());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return mime;
    }
}
