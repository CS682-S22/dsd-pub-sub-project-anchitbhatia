package api.broker;

import api.Connection;
import messages.Follower.FollowerRequest;
import messages.HeartBeat.HeartBeatMessage;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;

import java.io.IOException;

public abstract class BrokerState {
    private static final Logger LOGGER = LogManager.getLogger(BrokerState.class);
    protected Broker broker;

    public BrokerState(Broker broker) {
        this.broker = broker;
    }

    abstract void startBroker();

    abstract void handleFollowRequest(ClientHandler clientHandler, FollowerRequest request) throws IOException;

}