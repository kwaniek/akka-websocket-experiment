package pl.kw.akka.experiment.second;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.Optional;

/**
 * Created by Main_2 on 2017-05-14.
 */
public class ConnectedBot extends AbstractActor {

    private final ActorRef signInBot;

    public ConnectedBot(ActorRef signInBot) {
        this.signInBot = signInBot;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SignedIn.class, this::onSignedIn)
                .match(SignUserIn.class, this::onSignUserIn)
                .build();
    }

    private void onSignedIn(SignedIn signedIn) {
        System.out.println("Signed in " + Optional.of(signedIn.code));
    }

    private void onSignUserIn(SignUserIn signUserIn) {
        System.out.println("Trying to sign user in" + Optional.of(signUserIn.code));
        signInBot.tell(new SignInBot.ValidateInput(signUserIn.code), getSelf());
    }

    public static class SignUserIn {
        public final String code;

        public SignUserIn(String code) {
            this.code = code;
        }
    }

    public static class SignedIn {
        public final String code;

        public SignedIn(String code) {
            this.code = code;
        }
    }
}
