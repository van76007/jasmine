package www.jasmine.network;

public class ProcessPacketResult {
    private int sequence;
    private int ttl;
    private StringBuilder stringBuilder;

    public ProcessPacketResult(int sequence, int ttl) {
        this.sequence = sequence;
        this.ttl = ttl;
        this.stringBuilder = new StringBuilder();
    }

    public void increaseSequence(int amount) {
        this.sequence += amount;
    }

    public void increaseTtl(int amount) {
        this.ttl += amount;
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

    public String getReportMessage() {
        return stringBuilder.toString();
    }

    public void appendReportMessage(String reportMessage) {
        this.stringBuilder.append(reportMessage).append("\n");
    }

    @Override
    public String toString() {
        return "ProcessPacketResult{" +
                "sequence=" + sequence +
                ", ttl=" + ttl +
                ", report=\n" + stringBuilder.toString() +
                '}';
    }
}
