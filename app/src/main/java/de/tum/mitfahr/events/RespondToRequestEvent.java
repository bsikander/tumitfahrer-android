package de.tum.mitfahr.events;

/**
 * Created by amr on 21/06/14.
 */
public class RespondToRequestEvent extends AbstractEvent {

    public enum Type
    {
        RESPOND_SENT,
        RESPOND_FAILED,
        RESULT
    }

    public RespondToRequestEvent(Type type) {
        super(type);
    }
}
