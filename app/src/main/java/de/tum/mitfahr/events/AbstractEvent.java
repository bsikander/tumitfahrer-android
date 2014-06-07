package de.tum.mitfahr.events;

/**
 * Created by amr on 25/05/14.
 */
public abstract class AbstractEvent {
    private Enum _type;

    protected AbstractEvent(Enum type)
    {
        this._type = type;
    }

    public Enum getType()
    {
        return this._type;
    }
}
