package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.DeleteRideResponse;

/**
 * Created by amr on 15/06/14.
 */
public class DeleteRideEvent extends AbstractEvent {

    public enum Type
    {
        DELETE_SUCCESSFUL,
        DELETE_FAILED,
        RESULT
    }

    private DeleteRideResponse mDeleteRideResponse;

    public DeleteRideEvent(Type type, DeleteRideResponse deleteRideResponse) {
        super(type);
        this.mDeleteRideResponse = deleteRideResponse;
    }

    public DeleteRideResponse getResponse() {
        return this.mDeleteRideResponse;
    }
}
