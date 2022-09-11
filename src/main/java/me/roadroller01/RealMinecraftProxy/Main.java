package me.roadroller01.RealMinecraftProxy;

public class Main {
    public static void main(String[] args) throws Exception {
        String serverAddress = "127.0.0.1"; // 127.0.0.1
        int P2S = 25565; // 25565
        int C2P = 255;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-help") || args[i].equalsIgnoreCase("-h")){
                // TODO
                System.out.println("TODO");
                continue;
            }

            if (args[i].equalsIgnoreCase("-serveraddress") || args[i].equalsIgnoreCase("-sa") ){
                serverAddress = args[++i];
                continue;
            }

            if (args[i].equalsIgnoreCase("-p2s") || args[i].equalsIgnoreCase("-sp") ){
                P2S = Integer.parseInt(args[++i]);
                continue;
            }

            if (args[i].equalsIgnoreCase("-c2p") || args[i].equalsIgnoreCase( "-pp")){
                C2P = Integer.parseInt(args[++i]);
                continue;
            }
        }


        new ProxyServer(serverAddress,P2S,C2P).run();
    }
}
