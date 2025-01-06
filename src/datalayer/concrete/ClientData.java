package datalayer.concrete;

import java.io.PrintWriter;

public class ClientData {
    private String identifier;
    private PrintWriter writer;

    public ClientData(String identifier, PrintWriter writer) {
        this.identifier = identifier;
        this.writer = writer;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }
}