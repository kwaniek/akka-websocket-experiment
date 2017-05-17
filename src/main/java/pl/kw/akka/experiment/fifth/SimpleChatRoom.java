package pl.kw.akka.experiment.fifth;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.japi.JavaPartialFunction;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;

/**
 * Created by Main_2 on 2017-05-17.
 */
public class SimpleChatRoom {

    private final ActorRef chatRoomActor;
    private final String uuid;

    public SimpleChatRoom(ActorSystem actorSystem, String uuid) {
        this.chatRoomActor = actorSystem.actorOf(Props.create(SimpleChatBot.class, uuid));
        this.uuid = uuid;
    }

    public Flow<Message, Message, NotUsed> websocketFlow(String name) {
        return Flow.fromGraph(GraphDSL.create(buider -> {

            //input takes message
            Flow fromWebsocket = Flow.<Message>create()
                    .collect(new JavaPartialFunction<Message, Object>() {
                        @Override
                        public Object apply(Message x, boolean isCheck) throws Exception {
                            if (isCheck) {
                                throw noMatch();
                            } else if (x.asTextMessage().isStrict()) {
                                return new SimpleChatMessage(x.asTextMessage().getStrictText());
                            } else {
                                throw noMatch();
                            }
                        }
                    });
            buider.add(fromWebsocket);

            //output, return message
            Flow backToWebsocket = Flow.<SimpleChatMessage>create()
                    .collect(new JavaPartialFunction<SimpleChatMessage, Object>() {
                        @Override
                        public Object apply(SimpleChatMessage x, boolean isCheck) throws Exception {
                            return TextMessage.create(x.message);
                        }
                    });

            throw new IllegalStateException("Not implemented");
        }));
    }

    public void sendMessage(SimpleChatMessage message) {
        chatRoomActor.tell(message, ActorRef.noSender());
    }

}
