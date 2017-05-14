package pl.kw.akka.experiment.second;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.IOException;

/**
 * Created by Main_2 on 2017-05-14.
 */
public class ServerApplication {

    public static void main(String[] args) throws IOException {
        
        System.out.println("Starting actor system");
        final ActorSystem actorSystem = ActorSystem.create();

        final ActorRef connectionMaster = actorSystem
                .actorOf(
                        Props.create(ConnectionMaster.class));

        connectionMaster.tell(new ConnectionMaster.Start(), ActorRef.noSender());

        System.out.println("Any key + enter to stop");
        System.in.read();
        actorSystem.terminate();
    }
}
