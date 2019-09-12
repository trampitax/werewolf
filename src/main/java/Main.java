import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.*;

public class Main extends ListenerAdapter {
    private Map<String, Werewolf> games;

    public static void main(String[] args) throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(Token.getToken());
        Main myBot = new Main();
        builder.addEventListener(myBot);
        builder.buildAsync().getPresence().setGame(Game.of(Game.GameType.DEFAULT, "Prefix: !ww")); //Thanks Alex
    }

    Main() {
        games = new TreeMap<>();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message[] = event.getMessage().getContentDisplay().split(" ");
        String author = event.getAuthor().getName();


        String serverId = event.getGuild().getId();
//        System.out.println("SERVER --> "+event.getGuild().getName()+" "+serverId);

        if (!games.containsKey(serverId)) {
            games.put(serverId, new Werewolf());
        }

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
