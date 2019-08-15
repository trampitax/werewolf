import java.util.Timer;
import java.util.TimerTask;

//https://stackoverflow.com/questions/14393423/how-to-make-a-countdown-timer-in-java

public class Stopwatch {
    private int interval;
    private Timer timer;

    public void start(int seconds, Werewolf game, String action) {
        String secs = seconds + "";
        int delay = 1000;
        int period = 1000;
        this.timer = new Timer();
        this.interval = Integer.parseInt(secs);
        System.out.println(secs);
        this.timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                System.out.println(setInterval());
                if (interval == 0) {
                    System.out.println("finished!!");
                    switch (action) {
                        case "startGame":
                            if(!game.gameStarted) {
                                game.startGame();
                            }
                            break;
                        case "startNight":
                            System.out.println("Noche iniciada");
                            game.night();
                            break;
                        case "startDay":
                            System.out.println("Día finalizada");
                            game.day();
                            break;
                    }
                } else if (interval == 60 && action.equalsIgnoreCase("startGame")) {
                    game.channel.sendMessage(game.language.get("stopwatch1MinuteLeft")).queue();
                } else if (interval == 30 && !action.equalsIgnoreCase("startGame")) {
                    game.channel.sendMessage(game.language.get("stopwatch30SecsLeft")).queue();
                }
//                else if (interval > -10) {
//                    timer.cancel();
//                }
            }
        }, delay, period);
    }

    public void cancel() {
        this.timer.cancel();
    }

    public  int getInterval() {
        return interval;
    }

    private final int setInterval() {
        if (this.interval == 1)
            this.timer.cancel();
        return --this.interval;
    }
}