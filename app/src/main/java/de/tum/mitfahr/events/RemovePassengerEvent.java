package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RidesResponse;

/**
 * Created by amr on 28/06/14.
 */
public class RemovePassengerEvent extends AbstractEvent {

    public enum Type
    {
        SUCCESSFUL,
        FAILED,
        RESULT
    }


    public RemovePassengerEvent(Type type) {
        super(type);
    }
}
