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

                out.writeBytes("\n");
                if (file.exists()) {
                    System.out.println("the file exists");

                    sendBinaryFile(file, out);
                } else {
                    System.out.println("the file doesn't exist");
                    out.writeBytes("HTTP/1.1 404 NOT FOUND\n");
                    out.writeBytes("Content-length: " + message.length() +"\n");
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
            out.writeBytes("Content-length: " + file.length() +"\n");
            out.writeBytes("Content-type: " + getContentType(file) +"\n");

            String binary = Files.readString(file.toPath());
            out.writeBytes(binary +"\n");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static String getContentType(File file){
        String mime = "application/octet-stream";
        try{
            mime = Files.probeContentType(file.toPath());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return mime;
    }
}
