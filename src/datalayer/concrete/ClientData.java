package datalayer.concrete;

import java.io.PrintWriter;

/**
 * The ClientData class represents a client in the system,
 * storing the client's unique identifier and communication writer.
 */
public class ClientData 
{
    private String identifier; // Unique identifier for the client
    private PrintWriter writer; // Writer for sending messages to the client

    /**
     * Constructor to initialize a ClientData instance with an identifier and writer.
     *
     * @param identifier The unique identifier for the client.
     * @param writer     The writer used for client communication.
     */
    public ClientData(String identifier, PrintWriter writer) 
    {
        this.identifier = identifier;
        this.writer = writer;
    }

    /**
     * Retrieves the unique identifier for the client.
     *
     * @return The client's identifier.
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * Sets a new unique identifier for the client.
     *
     * @param identifier The new identifier for the client.
     */
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    /**
     * Retrieves the writer used for client communication.
     *
     * @return The PrintWriter for the client.
     */
    public PrintWriter getWriter()
    {
        return writer;
    }

    /**
     * Sets a new writer for the client.
     *
     * @param writer The new PrintWriter for the client.
     */
    public void setWriter(PrintWriter writer)
    {
        this.writer = writer;
    }
}
