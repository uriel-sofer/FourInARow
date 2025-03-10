import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsHandler {
    private static final Map<String, String> fileData = new TreeMap<>();  // Use a TreeMap to store player data sorted by name

    /**
     * Initiates a stats handling object
     * Reads the file content and stores it in a Map
     * @param statisticsFile The file to read the stats from
     */
    public static void initStatisticsHandler(File statisticsFile) {
        fileData.clear();
        String line = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(statisticsFile))) {
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);  // Split into player name and the rest
                if (parts.length == 2) {
                    fileData.put(parts[0].trim(), parts[1].trim());  // Store in the map
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage() + " Line: " + line);
        }
    }

    /**
     * Prints content from the stats file in a nicely formatted way.
     */
    public static void printStatsFromFile() {
        System.out.println("Player Statistics:");
        System.out.println("======================================================");
        System.out.printf("%-15s%-15s%-10s%-10s%n", "Player", "Total Games", "Wins", "Losses");
        System.out.println("------------------------------------------------------");

        for (Map.Entry<String, String> entry : fileData.entrySet()) {
            String[] parts = entry.getValue().split(",");
            if (parts.length >= 3) {
                System.out.printf("%-15s%-15s%-10s%-10s%n", entry.getKey(), parts[0], parts[1], parts[2]);
            } else {
                System.out.println("Error: Malformed data for player: " + entry.getKey());
            }
        }

        System.out.println("======================================================");
    }

    /**
     * Adds the players to the fileData map
     * @param players The players to add
     */
    public static void addPlayers(Player[] players) {
        for (Player p : players) {
            if (!fileData.containsKey(p.getPlayerName())) {
                fileData.put(p.getPlayerName(), "0,0,0");
            }
        }
    }

    /**
     * Prints local wins and losses for players
     * @param players The array of players
     */
    public static void printLocalWinsAndLosses(Player[] players) {
        System.out.println("The final score:");
        System.out.println("=================");

        for (Player p : players) {
            System.out.println(p.getPlayerName() + ":" + System.lineSeparator() +
                    "-----------------" + System.lineSeparator() +
                    "Wins:   " + p.wins + System.lineSeparator() +
                    "Losses: " + p.losses + System.lineSeparator() +
                    "=================");
        }
    }

    /**
     * Updates the corresponding lines in the map according to players' wins and losses
     * @param players Players array from game object
     */
    public static void updateTotalGames(Player[] players) {
        for (Player p : players) {
            if (fileData.containsKey(p.getPlayerName())) {
                String[] parts = fileData.get(p.getPlayerName()).split(",");
                if (parts.length >= 3) {
                    int totalGames = p.totalGames;
                    int wins = Integer.parseInt(parts[1].trim()) + p.wins;
                    int losses = Integer.parseInt(parts[2].trim()) + p.losses;

                    fileData.put(p.getPlayerName(), totalGames + "," + wins + "," + losses);
                }
            }
        }
    }

    /**
     * Looks through the map for how many games a player has played already, and returns it
     * @param playerName The player's name to get totalGames for
     * @return The total number of games the player has played according to the stats file
     */
    public static int getTotalGames(String playerName) {
        if (fileData.containsKey(playerName)) {
            String[] parts = fileData.get(playerName).split(",");
            return Integer.parseInt(parts[0].trim());
        }
        return 0;
    }

    /**
     * Writes the current map data into statsFile
     * @param statsFile The destination file
     */
    public static void writeToFile(File statsFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(statsFile))) {
            for (Map.Entry<String, String> entry : fileData.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
            System.out.println("Data successfully written to " + statsFile.getName());
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the statsFile: " + e.getMessage());
        }
    }
}
