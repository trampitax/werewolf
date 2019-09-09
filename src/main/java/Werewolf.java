import Language.Language;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

public class Werewolf {
    private MessageReceivedEvent event;
    public MessageChannel channel;
    private String authorName; // To make everything more comfortable
    private boolean gameCreated;
    public boolean gameStarted;
    private boolean gameEnded;
    private boolean firstNight;
    private boolean night;

    public Map<String, String> language;
    private String villageName;

    private boolean victimLastNight;
    private String victimName;

    private Map<String, Player> currentPlayers;
    private Map<Integer, Candidate> votations;


    public Werewolf() {
        this.language = Language.initializeMap();
        Language.changeLanguage("english", this.language);

        this.prepareGame();
        this.villageName = "Kebab";
    }

    public void director(MessageReceivedEvent theEvent, String[] message) {
        this.authorName = theEvent.getAuthor().getName();
        this.event = theEvent;

        if (this.event.getChannelType().toString().equals("TEXT")) {
            this.channel = this.event.getChannel();
            switch (message[1]) {
                case "create":
                    this.createGame();
                    break;
                case "join":
                    this.joinGame();
                    break;
                case "leave":
                    this.leaveGame();
                    break;
                case "forcestart":
                    this.forceStart();
                    break;
                case "list":
                    this.listPlayers();
                    break;
                case "vote":
                    this.vote(message[2]);
                    break;
                case "villagename":
                    this.changeVillageName(message[2]);
                    break;
                case "language":
                    if(message[2].equalsIgnoreCase("english") || message[2].equalsIgnoreCase("spanish")){
                        Language.changeLanguage(message[2], this.language);
                        channel.sendMessage(this.language.get("languageChangedSuccessful")).queue();
                    } else {
                        channel.sendMessage(this.language.get("languageChangedFailed")).queue();
                    }
                    break;
                case "info":
                    this.info();
                    break;
                case "help":
                    this.help();
                    break;
                default:
                    this.event.getChannel().sendMessage(this.language.get("understand")).queue();
            }
        } else if (this.event.getChannelType().toString().equals("PRIVATE")) {
            switch (message[1]) {
                case "kill":
                    this.kill(message[2]);
                    break;
                case "help":
                    this.help();
                    break;
                default:
                    this.event.getChannel().sendMessage(this.language.get("understand")).queue();
            }
        }
    }

    private void changeVillageName(String name) {
        String message = "";
        if (!this.gameStarted) {
            this.villageName = name;
            message = this.language.get("changeVillageNameSuccessful");
        } else {
            message = this.language.get("changeVillageNameFailed");
        }

        this.channel.sendMessage(message).queue();
    }

    private void prepareGame() {
        this.gameCreated = false;
        this.gameStarted = false;
        this.gameEnded = false;
        this.firstNight = true;
        this.victimLastNight = false;
        this.victimName = "";
    }

    private void forceStart() {
        if (!this.gameStarted && this.gameCreated) {
//            new Stopwatch.cancel();
//            this.startGame();
            channel.sendMessage("This option is unavailable at the moment!").queue();
        }
    }

    private void createGame() {
        if (!this.gameCreated) { //If the game is not created, we'll create one and initialize our map as the same time that we add the author of the message to it
            this.gameCreated = true;
            this.gameEnded = false;
            this.firstNight = true;

            this.currentPlayers = new TreeMap<>();
            this.votations = new TreeMap<>();

            this.currentPlayers.put(this.authorName, new Player(this.authorName, "Waiting Player", true, this.event.getAuthor()));
            this.channel.sendMessage(this.language.get("gameCreatedSuccessful")).queue();

            new Stopwatch().start(60 * 3, this, "startGame"); // Timer 3' 0''
        } else {
            this.channel.sendMessage(this.language.get("gameCreatedFailed")).queue();
        }
    }

    private void joinGame() {
        if (this.gameCreated && !this.gameStarted) {
            // Checking if the user is in the current game and if he's not, putting him in.
            if (!this.currentPlayers.containsKey(this.authorName)) {
                this.currentPlayers.put(this.authorName, new Player(this.authorName, "Waiting Player", true, this.event.getAuthor()));
                this.event.getChannel().sendMessage(this.authorName + this.language.get("joinGameSuccessful")).queue();
            } else {
                this.event.getChannel().sendMessage(this.authorName + this.language.get("joinGameAlreadyIn")).queue();
            }
        } else if (this.gameStarted) {
            this.event.getChannel().sendMessage(this.language.get("joinGameAlreadyStarted")).queue();
        } else {
            this.event.getChannel().sendMessage(this.language.get("joinGameNoGameCreated")).queue();
        }
    }

    private void leaveGame() {
        if (this.gameCreated && !this.gameStarted) {
            if (this.currentPlayers.containsKey(this.authorName)) {
                this.currentPlayers.remove(this.event.getAuthor().getName());
                this.event.getChannel().sendMessage(this.authorName + this.language.get("leaveGameSuccessful")).queue();
            } else {
                this.event.getChannel().sendMessage(this.authorName + this.language.get("leaveGameFailed")).queue();
            }
        } else if (this.gameCreated && this.gameStarted) {
            //TODO falta hacer que se borre tambien del juego mientras se esté jugando
        }
    }

    private void listPlayers() {
        String message = this.language.get("actualPlayers")+"\n";
        Set<String> playerNames = this.currentPlayers.keySet();

        String emoji = "";
        if (!this.gameEnded) {
            if (this.gameCreated && !this.gameStarted) {
                for (String name : playerNames) {
                    message += "- " + name + '\n';
                }
            } else if (this.gameCreated && this.gameStarted) {
                for (String name : playerNames) {
                    if (this.currentPlayers.get(name).getRole().equalsIgnoreCase(this.language.get("werewolf"))) {
                        emoji = " :wolf:";
                    } else {
                        emoji = " :bust_in_silhouette:";
                    }

                    if (this.currentPlayers.get(name).isAlive()) {
                        message += "- " + name + " :heart: \n";
                    } else {
                        message += "- " + name + " :skull: (" + this.currentPlayers.get(name).getRole() + emoji + ")\n";
                    }
                }
            } else {
                message = this.language.get("joinGameNoGameCreated");
            }
        } else {
            message = this.language.get("resultOfTheGame")+"\n";
            for (String name : playerNames) {
                if (this.currentPlayers.get(name).getRole().equalsIgnoreCase(this.language.get("werewolf"))) {
                    emoji = ":wolf:";
                } else {
                    emoji = ":bust_in_silhouette:";
                }
                if (this.currentPlayers.get(name).isAlive()) {
                    message += "- " + name + " :heart: (" + this.currentPlayers.get(name).getRole() + emoji + ")\n";
                } else {
                    message += "- " + name + " :skull: (" + this.currentPlayers.get(name).getRole() + emoji + ")\n";
                }
            }
        }
        this.channel.sendMessage(message).queue();
    }

    private void help() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Comandos");
        eb.addField("!ww create", this.language.get("helpCreate"), false);
        eb.addField("!ww join", this.language.get("helpJoin"), false);
        eb.addField("!ww leave", this.language.get("helpLeave"), false);
        eb.addField("!ww forcestart", this.language.get("helpForceStart"), false);
        eb.addField("!ww list", this.language.get("helpList"), false);
        eb.addBlankField(true);
        eb.addField("!ww vote", this.language.get("helpVote"), false);
        eb.addField("!ww kill", this.language.get("helpKill"), false);
        eb.addBlankField(true);
        eb.addField("!ww villagename name", this.language.get("helpVillageName"), false);
        eb.addField("!ww language English || Spanish", this.language.get("helpLanguage"), false);
        eb.addField("!ww help", this.language.get("helpHelp"), false);
        eb.addField("!ww info", this.language.get("helpInfo"), false);
        this.event.getChannel().sendMessage(eb.build()).queue();
    }

    private void info() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Werewolf Bot");
        eb.addField("Version", "1.3", true);
        eb.addField("Author", "trampitax", true);
        eb.addField("Github", "https://github.com/trampitax", true);
        eb.addField("Discord", "trampitax#8978", true);
        eb.setThumbnail("https://avatars0.githubusercontent.com/u/28568853?s=460&v=4");
        this.event.getChannel().sendMessage(eb.build()).queue();
    }

    public void startGame() {
        if (this.currentPlayers.size() < 4) {
            this.gameCreated = false;
            this.gameEnded = true;
            this.channel.sendMessage(this.language.get("minimumPlayers")).queue();
//            this.stopwatch.cancel();
        } else {
            this.gameStarted = true;

            int werewolves = 0;
            System.out.println(this.currentPlayers.size());
            if (this.currentPlayers.size() >= 4 && this.currentPlayers.size() < 6) {
                werewolves = 1;
            } else if (this.currentPlayers.size() >= 6 && this.currentPlayers.size() <= 11) {
                werewolves = 2;
            } else if (this.currentPlayers.size() > 11) {
                werewolves = 3;
            }

            ArrayList<String> usernames = new ArrayList<>(this.currentPlayers.keySet());
            Collections.shuffle(usernames);
            for (String name : usernames) {
                Player player = this.currentPlayers.get(name);
                if (werewolves != 0) {
                    player.setRole(this.language.get("werewolf"));
                    werewolves--;
                } else {
                    player.setRole(this.language.get("villager"));
                }

                if (!player.getDiscordUser().isBot()) {
                    player.getDiscordUser().openPrivateChannel().queue((channel) -> channel.sendMessage(this.language.get("gameStartedAndYouAre") + player.getRole()).queue());
                }
            }

            this.night();
        }
    }

    private void vote(String vote) {
        String message = "";
        if (this.gameStarted && this.currentPlayers.containsKey(this.authorName) && !this.currentPlayers.get(this.authorName).isAlreadyVoted() && !this.night && this.currentPlayers.get(this.authorName).isAlive()) {
            if (this.votations.containsKey(Integer.parseInt(vote)) && !this.votations.get(Integer.parseInt(vote)).getUsername().equalsIgnoreCase(this.authorName)) {
                this.votations.get(Integer.parseInt(vote)).vote();
                this.currentPlayers.get(this.authorName).setAlreadyVoted(true);

                message = this.authorName + this.language.get("voteSuccessful") + this.votations.get(Integer.parseInt(vote)).getUsername() + "!\n\n";
                for (Map.Entry<Integer, Candidate> candidate : this.votations.entrySet()) {
                    message += candidate.getKey() + " - " + candidate.getValue().getUsername() + "\t-->\t" + candidate.getValue().getVotes() + '\n';
                }

            } else if (!this.votations.containsKey(Integer.parseInt(vote))) {
                message = this.language.get("playerDoesntExists");
            } else {
                message = this.language.get("votingOwnSelf");
            }
        } else if (!this.gameStarted || !this.gameCreated) {
            message = this.language.get("joinGameNoGameCreated");
        } else if (!this.currentPlayers.containsKey(this.authorName)) {
            message = this.language.get("leaveGameFailed");
        } else if (this.currentPlayers.get(this.authorName).isAlreadyVoted()) {
            message = this.language.get("alreadyVotedThisRound");
        } else if (this.night) {
            message = this.language.get("votingAtNight");
        } else if (!this.currentPlayers.get(this.authorName).isAlive()) {
            message = this.language.get("votingDead");
        }

        this.event.getChannel().sendMessage(message).queue();
    }

    private void kill(String victim) {
        String message = "";
        if (this.gameStarted && this.currentPlayers.containsKey(this.authorName) && !this.currentPlayers.get(this.authorName).isAlreadyVoted() && this.night && this.currentPlayers.get(this.authorName).isAlive()) {
            if (this.votations.containsKey(Integer.parseInt(victim)) && !this.votations.get(Integer.parseInt(victim)).getUsername().equalsIgnoreCase(this.authorName)) {

                if (this.currentPlayers.get(this.authorName).getRole().equalsIgnoreCase(this.language.get("werewolf"))) {
                    this.votations.get(Integer.parseInt(victim)).vote();
                    this.currentPlayers.get(this.authorName).setAlreadyVoted(true);

                    message = this.language.get("killVoteSucessful") + this.votations.get(Integer.parseInt(victim)).getUsername() + "!\n\n";
                } else if (!this.currentPlayers.get(this.authorName).getRole().equalsIgnoreCase(this.language.get("werewolf"))) {
                    message = this.language.get("killVoteFailed");
                }
                //TODO deberian todos los hombres lobo saber quienes son hombres lobo?

            } else if (!this.votations.containsKey(Integer.parseInt(victim))) {
                message = this.language.get("playerDoesntExists");
            } else {
                message = this.language.get("votingOwnSelf");
            }
        } else if (!this.gameStarted || !this.gameCreated) {
            message = this.language.get("joinGameNoGameCreated");
        } else if (!this.currentPlayers.containsKey(this.authorName)) {
            message = this.language.get("leaveGameFailed");
        } else if (this.currentPlayers.get(this.authorName).isAlreadyVoted()) {
            message = this.language.get("alreadyVotedThisRound");
        } else if (!this.night) {
            message = this.language.get("killingAtDay");
        } else if (!this.currentPlayers.get(this.authorName).isAlive()) {
            message = this.language.get("votingDead");
        }

        this.event.getChannel().sendMessage(message).queue();
    }

    public void night() {
        this.night = true;
        // Timer 1' 30''

        if (!this.firstNight) {
            this.victimName = getResult();
            this.checkWin();

            if (this.victimLastNight && !this.gameEnded) {
                this.event.getChannel().sendMessage(this.victimName + this.language.get("nightVictimSuccessful") + this.currentPlayers.get(this.victimName).getRole() + '!').queue();
                this.listPlayers();
            } else if (!this.victimLastNight) {
                this.event.getChannel().sendMessage(this.language.get("nightVictimNone") + this.villageName + this.language.get("nightVictimNone2")).queue();
            }
        }


        if (!this.gameEnded) {
            this.event.getChannel().sendMessage(this.language.get("nightStarting") + this.villageName + this.language.get("nightStarting2") + "\n\n" + this.language.get("nightStarting3")).queue();

            this.firstNight = false;
            //Creating the votation box which contains the alive players
            this.votations = new TreeMap<>();
            int id = 1;
            for (Player player : this.currentPlayers.values()) {
                player.setAlreadyVoted(false);
                if (player.isAlive() && !player.getRole().equalsIgnoreCase(this.language.get("werewolf"))) {
                    this.votations.put(id, new Candidate(player.getUsername()));
                    id++;
                }
            }

            String candidates = this.language.get("nightCandidatesMessage") + "\n";
            //Creating the list of candidates
            for (Map.Entry<Integer, Candidate> user : this.votations.entrySet()) {
                candidates += user.getKey() + " - " + user.getValue().getUsername() + '\n';
            }

            for (Player player : this.currentPlayers.values()) {
                if (player.getRole().equalsIgnoreCase(this.language.get("werewolf"))) {
                    String finalCandidates = candidates;
                    player.getDiscordUser().openPrivateChannel().queue((channel) -> channel.sendMessage(finalCandidates).queue());
                }
            }

            // Now werewolves have to vote someone
            new Stopwatch().start(90, this, "startDay"); // Timer 1' 30''
        } else {
            System.out.println("kek");
            this.prepareGame();
        }
    }

    public void day() {
        this.night = false;
        String message = "";
        // Timer 2' 00''

        this.victimName = getResult();
        this.checkWin();

        if (!this.gameEnded) {
            if (this.victimLastNight) {
                message = this.language.get("morningVictimSuccessful") + this.villageName + this.language.get("morningVictimSuccessful2") + this.victimName + this.language.get("morningVictimSuccessful3");
                this.listPlayers();
            } else {
                message = this.language.get("morningVictimNone") + this.villageName + this.language.get("morningVictimNone2");
            }

            message += "\n" + this.language.get("morningVictimFinal") + "\n\n" + this.language.get("morningVictimFinal2");

            this.channel.sendMessage(message).queue();

            //Creating the votation box which contains the alive players
            this.votations = new TreeMap<>();
            int id = 1;
            for (Player player : this.currentPlayers.values()) {
                if (player.isAlive()) {
                    player.setAlreadyVoted(false);
                    this.votations.put(id, new Candidate(player.getUsername()));
                    id++;
                }
            }

            String candidates = "";
            //Creating the list of candidates
            for (Map.Entry<Integer, Candidate> user : this.votations.entrySet()) {
                candidates += user.getKey() + " - " + user.getValue().getUsername() + '\n';
            }

            this.channel.sendMessage(candidates).queue();
            // Now all users have to vote someone
            new Stopwatch().start(60 * 2, this, "startNight"); // Timer 2' 00''
        } else {
            this.prepareGame();
        }
    }

    private String getResult() { //TODO Think in a better name for this
        Collection<Candidate> candidates = this.votations.values();
        String winnerName = "";
        int winnerScore = 0;

        for (Candidate candidate : candidates) {
            if (candidate.getVotes() > winnerScore) {
                winnerScore = candidate.getVotes();
                winnerName = candidate.getUsername();
            }
        }

        // If nobody has been voted, that would mean that nobody died
        if (winnerScore == 0) {
            this.victimLastNight = false;
        } else {
            this.victimLastNight = true;
        }

        // Also if nobody died there isn't any victim, so it can't have a name
        if (!winnerName.isEmpty()) {
            this.currentPlayers.get(winnerName).setAlive(false);  //The reward for winning is death kek
        }

        return winnerName;
    }

    private void checkWin() {
        int aliveVillagers = 0;
        int aliveWerewolves = 0;

        for (Player player : this.currentPlayers.values()) {
            if (player.isAlive()) {
                if(player.getRole().equalsIgnoreCase(this.language.get("werewolf"))) {
                    aliveWerewolves++;
                } else {
                    aliveVillagers++;
                }
            }
        }


        if (aliveVillagers == 0 || (aliveVillagers == 1 && aliveWerewolves == 1)) {
            this.gameEnded = true;
            this.channel.sendMessage(this.language.get("winWerewolves")).queue();
            this.listPlayers();
        } else if (aliveWerewolves == 0) {
            this.gameEnded = true;
            this.channel.sendMessage(this.language.get("winVillagers")).queue();
            this.listPlayers();
        }
    }
}
