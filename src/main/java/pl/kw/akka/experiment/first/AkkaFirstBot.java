package pl.kw.akka.experiment.first;

import akka.actor.AbstractActor;

import java.util.Optional;

/**
 * Created by Main_2 on 2017-05-14.
 */
public class AkkaFirstBot extends AbstractActor {

    private Optional<Direction> direction = Optional.empty();
    private boolean moving = false;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Move.class, this::onMove)
                .match(Stop.class, this::onStop)
                .build();
    }

    public enum Direction {UP, DOWN, LEFT, RIGHT}

    public static class Move {
        public final Direction direction;

        public Move(Direction direction) {
            this.direction = direction;
        }
    }

    private void onMove(Move move) {
        this.moving = true;
        System.out.println(getSelf().path() + ": I am moving to " + Optional.of(move.direction));
        if (Math.random() > 0.5) {
            getContext().stop(getSelf());
        }
    }

    private void onStop(Stop stop) {
        this.moving = false;
        System.out.println("I am stopping now");
    }


    public static class Stop {
        
    }

    public static class GetRobotState {

    }

    public static class RobotState {
        public final Direction direction;
        public final boolean moving;

        public RobotState(Direction direction, boolean moving) {
            this.direction = direction;
            this.moving = moving;
        }
    }
}
