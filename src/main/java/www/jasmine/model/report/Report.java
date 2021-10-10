package www.jasmine.model.report;

import www.jasmine.Command;

public class Report {
    private String host;
    private Command command;
    private String message;


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
                ", command=" + command +
                ", message='" + message + '\'' +
                '}';
    }
}