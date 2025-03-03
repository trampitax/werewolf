package Language;

import java.io.*;
import java.util.*;

public class Language {
    public static Map<String, String> initializeMap(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("understand", "");

        map.put("changeVillageNameSuccessful", "");
        map.put("changeVillageNameFailed", "");

        map.put("gameCreatedSuccessful", "");
        map.put("gameCreatedFailed", "");

        map.put("joinGameSuccessful", ""); // Line 6
        map.put("joinGameAlreadyIn", ""); // Line 7

        map.put("joinGameAlreadyStarted", "");
        map.put("joinGameNoGameCreated", "");

        map.put("leaveGameSuccessful", ""); // Line 10
        map.put("leaveGameFailed", ""); // Line 11

        map.put("actualPlayers", "");
        map.put("resultOfTheGame", "");

        map.put("helpCreate", "");
        map.put("helpJoin", "");
        map.put("helpLeave", "");
        map.put("helpForceStart", "");
        map.put("helpList", "");

        map.put("helpVote", "");
        map.put("helpKill", "");

        map.put("helpVillageName", "");
        map.put("helpLanguage", "");
        map.put("helpHelp", "");
        map.put("helpInfo", "");

        map.put("minimumPlayers", "");

        map.put("voteSuccessful", ""); // Line 26
        map.put("playerDoesntExists", "");
        map.put("votingOwnSelf", "");
        map.put("alreadyVotedThisRound", "");
        map.put("votingAtNight", "");
        map.put("votingDead", "");

        map.put("killVoteSucessful", ""); // Line 32
        map.put("killVoteFailed", "");
        map.put("killingAtDay", "");

        map.put("nightVictimSuccessful", ""); // Line 35
        map.put("nightVictimNone", ""); // Line 36

        map.put("nightStarting", ""); // Line 37
        map.put("nightStarting2", ""); // Line 38

        map.put("nightCandidatesMessage", "");

        map.put("morningVictimSuccessful", ""); // Line 40

        map.put("morningVictimNone", ""); // Line 41

        map.put("morningVictimFinal", "");
        map.put("morningVictimFinal2", ""); // KEEP Line 43

        map.put("winWerewolves", "");
        map.put("winVillagers", "");

        map.put("languageChangedSuccessful", "");
        map.put("languageChangedFailed", "");

        map.put("stopwatch1MinuteLeft","");
        map.put("stopwatch30SecsLeft","");

        map.put("onlyPlayAtWerewolfChannel","");

        map.put("gameStartedAndYouAre",""); // Line 51


        map.put("villager","");
        map.put("werewolf","");

        return map;
    }

    public static void changeLanguage(String language, Map<String, String> map) {
       BufferedReader br = null;

        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        try {
            File file = new File("src\\main\\java\\Language\\" + language + ".txt");
            if(!file.exists()) {
                file = new File("/home/roberto/languages/" + language + ".txt"); //TODO Improve this
            }

            br = new BufferedReader(new FileReader(file));

            String line = br.readLine();

            while (line != null && it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                map.put(pair.getKey(), line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert br != null;
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
