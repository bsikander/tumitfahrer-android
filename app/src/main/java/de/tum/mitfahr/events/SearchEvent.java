package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.SearchResponse;

/**
 * Created by amr on 31/05/14.
 */
public class SearchEvent extends AbstractEvent {

    public enum Type
    {
        SEARCH_SUCCESSFUL,
        SEARCH_FAILED,
        RESULT
    }

    private SearchResponse mSearchResponse;

    public SearchEvent(Type type, SearchResponse searchResponse) {
        super(type);
        this.mSearchResponse = searchResponse;
    }

    public SearchResponse getResponse() {
        return this.mSearchResponse;
    }
}
