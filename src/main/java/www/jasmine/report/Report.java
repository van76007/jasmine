package www.jasmine.report;

import www.jasmine.Command;

public class Report {
    private String host;
    private String message;
    private Command command;

    public Report(String host, String message, Command command) {
        this.host = host;
        this.message = message;
        this.command = command;
    }

    public String getMessage() {
        return message;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "Report{" +
                "host='" + host + '\'' +
                ", message=\n'" + message + '\'' +
                '}';
    }
}