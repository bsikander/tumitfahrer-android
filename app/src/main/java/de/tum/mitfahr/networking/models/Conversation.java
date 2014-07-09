package de.tum.mitfahr.networking.models;

import java.util.ArrayList;

/**
 * Created by amr on 07.07.14.
 */
public class Conversation {

    private int id;
    private int userId;
    private int otherUserId;
    private Ride ride;
    private ArrayList<Message> messages;

    public Conversation(int id, int userId, int otherUserId, Ride ride, ArrayList<Message> messages) {
        this.id = id;
        this.userId = userId;
        this.otherUserId = otherUserId;
        this.ride = ride;
        this.messages = messages;
    }
}
