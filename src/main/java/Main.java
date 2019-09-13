import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.*;

public class Main extends ListenerAdapter {
    private Map<String, Werewolf> games;
    private Map<String, ArrayList<String>> userServers;

    public static void main(String[] args) throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(Token.getToken());
        Main myBot = new Main();
        builder.addEventListener(myBot);
        builder.buildAsync().getPresence().setGame(Game.of(Game.GameType.DEFAULT, "Prefix: !ww")); //Thanks Alex
    }

    Main() {
        games = new TreeMap<>();
        userServers = new TreeMap<>();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message[] = event.getMessage().getContentDisplay().split(" ");
        String author = event.getAuthor().getName();

        String serverId = "";
        if(!event.getChannelType().toString().equals("PRIVATE")) {
            serverId = event.getGuild().getId();
        }
        String userId = event.getAuthor().getId();

//        System.out.println("SERVER --> "+event.getGuild().getName()+" "+serverId);

        if (!games.containsKey(serverId)) {
            games.put(serverId, new Werewolf());
        }


        if (!userServers.containsKey(userId)) {
            userServers.put(userId, new ArrayList<>());
        }
        if (!userServers.get(userId).contains(serverId)) {
            userServers.get(userId).add(serverId);
        }

        if(event.getChannelType().toString().equals("PRIVATE") && !event.getAuthor().isBot()) {
            for(String id : userServers.get(userId)) {
                if(games.get(id).gameStarted) {
                    games.get(id).director(event, message);
                    break;
                }
            }
        } else if (event.getChannelType().toString().equals("TEXT") && !event.getAuthor().isBot()) {
            switch (message[0]) {
                case "!ww":
                    if (event.getChannel().getName().equalsIgnoreCase("werewolf")) {
                        games.get(serverId).director(event, message);
                    } else {
                        event.getChannel().sendMessage(games.get(serverId).language.get("onlyPlayAtWerewolfChannel")).queue();
                    }
                    break;
//            case "!talk":
//                event.getChannel().sendMessage(event.getMessage().getContentDisplay().substring(5)).queue();
//                break;
                case "!closeww":
                    System.exit(0);
                    break;
            }
        }
    }
}

// Nuevo mapa <USERID, ArrayList<SERVERID>
// Se colocase una ID unica delante del mensaje al contestar por privado
