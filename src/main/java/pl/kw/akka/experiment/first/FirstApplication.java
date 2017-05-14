package pl.kw.akka.experiment.first;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.IOException;

/**
 * Created by Main_2 on 2017-05-14.
 */
public class FirstApplication {

    public static void main(String[] args) throws IOException {
        final ActorSystem actorSystem = ActorSystem.create();

        final ActorRef botMaster = actorSystem.actorOf(
                Props.create(AkkaBotMaster.class),
                "botMaster");

        botMaster.tell(new AkkaBotMaster.StartChildBots(), ActorRef.noSender());

        System.out.println("Any key to terminate");
        System.in.read();
        System.out.println("Shutting down actor system");
        actorSystem.terminate();
    }
}
