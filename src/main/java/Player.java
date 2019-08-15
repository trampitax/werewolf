import net.dv8tion.jda.core.entities.User;

public class Player {
    private String username;
    private String role;
    private boolean alive;
    private User discordUser;
    private boolean alreadyVoted;

    public Player(String username, String role, boolean alive, User discordUser) {
        this.username = username;
        this.role = role;
        this.alive = alive;
        this.discordUser = discordUser;
        this.alreadyVoted = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public User getDiscordUser() {
        return discordUser;
    }

    public void setDiscordUser(User discordUser) {
        this.discordUser = discordUser;
    }

    public boolean isAlreadyVoted() {
        return alreadyVoted;
    }

    public void setAlreadyVoted(boolean alreadyVoted) {
        this.alreadyVoted = alreadyVoted;
    }
}
