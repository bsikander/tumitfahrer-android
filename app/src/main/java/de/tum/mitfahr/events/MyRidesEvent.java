package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.MyRidesResponse;

/**
 * Created by amr on 15/06/14.
 */
public class MyRidesEvent extends AbstractEvent {

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private MyRidesResponse mMyRidesResponse;

    public MyRidesEvent(Type type, MyRidesResponse myRidesResponse) {
        super(type);
        this.mMyRidesResponse = myRidesResponse;
    }

    public MyRidesResponse getResponse() {
        return this.mMyRidesResponse;
    }
}
