package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.networking.api.MessagesAPIService;

/**
 * Created by amr on 07.07.14.
 */
public class MessagesRESTClient extends AbstractRESTClient {

    private MessagesAPIService messagesAPIService;

    public MessagesRESTClient(String mBaseBackendURL) {

        super(mBaseBackendURL);
        messagesAPIService = mRestAdapter.create(MessagesAPIService.class);
    }

//    public void createMessage(String userAPIKey, int rideId, int conversationId,
//                              MessageRequest message) {
//        messagesAPIService.createMessage(userAPIKey, rideId, conversationId, message,
//                createMessageCallback);
//    }
}
