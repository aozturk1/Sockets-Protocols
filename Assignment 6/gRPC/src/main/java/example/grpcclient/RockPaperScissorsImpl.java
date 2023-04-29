package example.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

import java.util.*;

public class RockPaperScissorsImpl extends RockPaperScissorsGrpc.RockPaperScissorsImplBase {

    private final Map<String, Player> players = new HashMap<>();

    @Override
    public void play(PlayReq request, StreamObserver<PlayRes> responseObserver) {
        // Retrieve player from the map or create a new one
        Player player = players.computeIfAbsent(request.getName(), name -> new Player(name));

        // Generate a random play for the computer
        PlayReq.Played computerPlay = PlayReq.Played.values()[new Random().nextInt(3)];

        // Determine the outcome of the game
        Outcome outcome = player.play(request.getPlay(), computerPlay);

        // Prepare the response
        PlayRes.Builder builder = PlayRes.newBuilder();
        builder.setIsSuccess(true);
        builder.setWin(outcome == Outcome.WIN);
        builder.setMessage(String.format("Computer played %s, you played %s, you %s",
                computerPlay.name().toLowerCase(),
                request.getPlay().name().toLowerCase(),
                outcome == Outcome.WIN ? "win" : outcome == Outcome.LOSS ? "lose" : "tie"));

        // Send the response back to the client
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        //System.out.println(players);
    }

    @Override
    public void leaderboard(Empty request, StreamObserver<LeaderboardRes> responseObserver) {
        // Sort the players by number of wins
        List<Player> sortedPlayers = new ArrayList<>(players.values());
        sortedPlayers.sort(Comparator.comparingInt(Player::getWins).reversed());

        // Prepare the leaderboard response
        LeaderboardRes.Builder builder = LeaderboardRes.newBuilder();
        builder.setIsSuccess(true);
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);
            LeaderboardEntry entry = LeaderboardEntry.newBuilder()
                    .setName(player.getName())
                    .setRank(i + 1)
                    .setWins(player.getWins())
                    .setLost(player.getLosses())
                    .build();
            builder.addLeaderboard(entry);
        }

        // Send the response back to the client
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private static class Player {
        private final String name;
        private int wins;
        private int losses;

        public Player(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getWins() {
            return wins;
        }

        public int getLosses() {
            return losses;
        }

        public Outcome play(PlayReq.Played play, PlayReq.Played computerPlay) {
            if (play == computerPlay) {
                return Outcome.DRAW;
            } else if ((play == PlayReq.Played.ROCK && computerPlay == PlayReq.Played.SCISSORS) ||
                    (play == PlayReq.Played.PAPER && computerPlay == PlayReq.Played.ROCK) ||
                    (play == PlayReq.Played.SCISSORS && computerPlay == PlayReq.Played.PAPER)) {
                this.wins++;
                return Outcome.WIN;
            } else {
                this.losses++;
                return Outcome.LOSS;
            }
        }
    }

    public enum Outcome {
        WIN, LOSS, DRAW
    }
}