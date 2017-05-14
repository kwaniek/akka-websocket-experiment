package pl.kw.akka.experiment.first;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;

/**
 * Created by Main_2 on 2017-05-14.
 */
public class AkkaBotMaster extends AbstractActor {

    public AkkaBotMaster() {
        for (int i = 0; i < 10; i++) {
            final ActorRef child = getContext().actorOf(Props.create(AkkaFirstBot.class));
            getContext().watch(child);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartChildBots.class, this::onStartChildBots)
                .match(Terminated.class, this::onChildTerminated)
                .build();
    }

    private void onChildTerminated(Terminated terminated) {
        System.out.println("Child has stopped, creating new one");
        final ActorRef child = getContext().actorOf(Props.create(AkkaFirstBot.class));
        getContext().watch(child);
    }

    private void onStartChildBots(StartChildBots startChildBots) {
        final AkkaFirstBot.Move move = new AkkaFirstBot.Move(AkkaFirstBot.Direction.UP);
        for (ActorRef child : getContext().getChildren()) {
            System.out.println("Starting child " + child);
            child.tell(move, getSelf());
        }
    }

    public static class StartChildBots {
    }
}
