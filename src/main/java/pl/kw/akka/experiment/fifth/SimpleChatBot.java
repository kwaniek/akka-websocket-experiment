package pl.kw.akka.experiment.fifth;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Main_2 on 2017-05-17.
 */
public class SimpleChatBot extends AbstractActor {

    private final String uuid;
    private final Map<String, ActorRef> users = new HashMap<>();

    public SimpleChatBot(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UserJoined.class, this::onUserJoined)
                .match(UserLeft.class, this::onUserLeft)
                .match(IncomingMessage.class, this::onIncomingMessage)
                .build();
    }

    public void onIncomingMessage(IncomingMessage incomingMessage) {
        broadcast(incomingMessage.chatMessage);
    }

    public void onUserLeft(UserLeft userLeft) {
        System.out.println("User " + Optional.of(userLeft.name) + " left");
        broadcast(new SimpleChatMessage("User " + userLeft.name + " left channel"));
        users.remove(userLeft.name);
    }

    public void onUserJoined(UserJoined userJoined) {
        System.out.println("User " + Optional.of(userJoined.name) + " joined");
        users.put(userJoined.name, userJoined.user);
        broadcast(new SimpleChatMessage("User " + userJoined.name + " joined channel"));
    }

    public void broadcast(SimpleChatMessage msg) {
        users.entrySet().forEach(entry ->
                entry.getValue().tell(msg, ActorRef.noSender()));
    }

    public static class UserJoined {

        public final ActorRef user;
        public final String name;

        public UserJoined(ActorRef user, String name) {
            this.user = user;
            this.name = name;
        }
    }

    public static class UserLeft {

        public final String name;

        public UserLeft(String name) {
            this.name = name;
        }
    }

    public static class IncomingMessage {

        public final SimpleChatMessage chatMessage;

        public IncomingMessage(SimpleChatMessage chatMessage) {
            this.chatMessage = chatMessage;
        }
    }
}
