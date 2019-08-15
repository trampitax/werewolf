public class Candidate {
    private String username;
    private int votes;

    public Candidate(String username) {
        this.username = username;
        this.votes = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVotes() {
        return votes;
    }

    public void vote() {
        this.votes++;
    }
}
