package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RidesResponse;

/**
 * Created by amr on 28/06/14.
 */
public class DeleteRideRequestEvent extends AbstractEvent {

    public enum Type
    {
        SUCCESSFUL,
        FAILED,
        RESULT
    }

    public DeleteRideRequestEvent(Type type) {
        super(type);
    }
}
