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

                if (path.equals("") || path.charAt(path.length() - 1) == '/') {
                    path += "index.html";
                }

                File file = new File("docs/" + path);
                System.out.println(path);

                if(path.equals("test")){
                    out.writeBytes("HTTP/1.1 301 MOVED PERMANENTLY\n");
                    out.writeBytes("Location: https://google.com\n");
                    out.writeBytes("\n");
                }

                if (file.exists()) {
                    System.out.println("the file exists");
                    sendBinaryFile(client, file);
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

    private static void sendBinaryFile(Socket socket, File file) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.writeBytes("HTTP/1.1 200 OK\n");
        out.writeBytes("Content-Length: " + file.length() + "\n");
        out.writeBytes(getContentType(file) + "\n");
        out.writeBytes("\n");

        InputStream input = new FileInputStream(file);
        byte[] buf = new byte[8192];
        int n;
        while ((n = input.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        input.close();
    }

    private static String getContentType(File file) {
        String filename = file.getName();
        String[] temp = filename.split("\\.");
        String extension = temp[temp.length - 1];
        switch (extension) {
            case "html":
                return "Content-Type: text/html";
            case "png":
                return "Content-Type: image/png";
            case "jpeg":
            case "jpg":
                return "Content-Type: image/jpeg";
            case "css":
                return "Content-Type: text/css";
            default:
                return "Content-Type: text/plain";
        }
    }
}
