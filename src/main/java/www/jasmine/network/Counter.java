package www.jasmine.network;

public class Counter {
    private int sequence;
    private int ttl;

    public Counter(int sequence, int ttl) {
        this.sequence = sequence;
        this.ttl = ttl;
    }

    public int getSequence() {
        return sequence;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public void increaseSequence(int amount) {
        this.sequence += amount;
    }

    public void increaseTtl(int amount) {
        this.ttl += amount;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "sequence=" + sequence +
                ", ttl=" + ttl +
                '}';
    }
}
