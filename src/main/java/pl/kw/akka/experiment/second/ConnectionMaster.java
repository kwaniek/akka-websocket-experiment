package pl.kw.akka.experiment.second;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.Random;

/**
 * Created by Main_2 on 2017-05-14.
 */
public class ConnectionMaster extends AbstractActor {

    public ConnectionMaster() {
        ActorRef signInBot = getContext().actorOf(Props.create(SignInBot.class));
        for (int i = 0; i < 5; i++) {
            getContext().actorOf(Props.create(ConnectedBot.class, signInBot));
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Start.class, this::onStart)
                .build();
    }

    private void onStart(Start start) {
        for (ActorRef ref : getContext().getChildren()) {

            ref.tell(
                    new ConnectedBot.SignUserIn(
                            Long.toHexString(
                                    new Random().nextLong()
                            )
                    ),
                    ActorRef.noSender()
            );
        }
    }

    public static class Start {
    }
}
