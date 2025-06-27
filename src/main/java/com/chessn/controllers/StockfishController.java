package com.chessn.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockfishController {

    private Process stockfishProcess;
    private BufferedReader reader;
    private OutputStreamWriter writer;

    private static final String STOCKFISH_PATH = "engine/stockfish.exe";

    public StockfishController() {
        startEngine();
    }

    public void startEngine() {
        try {
            ProcessBuilder pb = new ProcessBuilder(STOCKFISH_PATH);
            stockfishProcess = pb.start();
            reader = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
            writer = new OutputStreamWriter(stockfishProcess.getOutputStream());
            System.out.println("Stockfish engine starting...");
            
            // Wait for engine to be ready by sending "isready" and waiting for "readyok"
            sendCommand("isready");
            String line;
            while ((line = reader.readLine()) != null) {
                if ("readyok".equals(line)) {
                    System.out.println("Stockfish engine is ready.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command) {
        try {
            writer.write(command + "\n");
            writer.flush();
            System.out.println("Sent command: " + command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBestMove(String fen, int level, int depth, int multiPV) {
        // Set the skill level (0-20). This affects evaluation.
        sendCommand("setoption name Skill Level value " + level);
        // Set number of lines to consider
        sendCommand("setoption name MultiPV value " + multiPV);
        
        // Position the board
        sendCommand("position fen " + fen);
        
        // Tell the engine to think to a certain depth
        sendCommand("go depth " + depth);
        
        List<String> moves = new ArrayList<>();
        Pattern movePattern = Pattern.compile("info depth \\d+ seldepth \\d+ multipv \\d+ score (cp|mate) -?\\d+ nodes \\d+ nps \\d+ hashfull \\d+ tbhits \\d+ time \\d+ pv (\\w{4,5})");

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                Matcher matcher = movePattern.matcher(line);
                if (matcher.find()) {
                    moves.add(matcher.group(2));
                }
                // The "bestmove" line indicates the engine is done thinking
                if (line.startsWith("bestmove")) {
                    break; 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // If we found moves, pick one at random
        if (!moves.isEmpty()) {
            Random rand = new Random();
            return moves.get(rand.nextInt(moves.size()));
        }

        return null; // Return null if no move was found
    }
    
    public void stopEngine() {
        try {
            sendCommand("quit");
            reader.close();
            writer.close();
            stockfishProcess.destroy();
            System.out.println("Stockfish engine stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 