import com.google.inject.Guice;
import com.google.inject.Injector;
import model.elevator.Elevator;
import model.request.ExternalUserRequest;
import resource.ElevatorResource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class App {
    public static void main(String[] args) {
        final Injector injector = Guice.createInjector(new ElevatorModule());
        final ElevatorResource elevatorResource = injector.getInstance(ElevatorResource.class);

        final List<Elevator> elevators = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            elevators.add(elevatorResource.addElevator(10));
        }

        CompletableFuture.runAsync(() -> elevatorResource.getElevator(elevators.get(0).getElevatorId(), ExternalUserRequest.builder().userId(1).dropFloor(5).pickupFloor(1).build()));
        CompletableFuture.runAsync(() -> elevatorResource.getElevator(elevators.get(0).getElevatorId(), ExternalUserRequest.builder().userId(2).dropFloor(1).pickupFloor(7).build()));
        CompletableFuture.runAsync(() -> elevatorResource.getElevator(elevators.get(0).getElevatorId(), ExternalUserRequest.builder().userId(3).dropFloor(5).pickupFloor(10).build()));
        CompletableFuture.runAsync(() -> elevatorResource.getElevator(elevators.get(0).getElevatorId(), ExternalUserRequest.builder().userId(1).dropFloor(5).pickupFloor(2).build()));

        CompletableFuture.runAsync(() -> elevatorResource.getElevator(elevators.get(3).getElevatorId(), ExternalUserRequest.builder().userId(1).dropFloor(5).pickupFloor(1).build()));
        CompletableFuture.runAsync(() -> elevatorResource.getElevator(elevators.get(1).getElevatorId(), ExternalUserRequest.builder().userId(2).dropFloor(1).pickupFloor(7).build()));
        CompletableFuture.runAsync(() -> elevatorResource.getElevator(elevators.get(0).getElevatorId(), ExternalUserRequest.builder().userId(3).dropFloor(5).pickupFloor(10).build()));
        CompletableFuture.runAsync(() -> elevatorResource.getElevator(elevators.get(3).getElevatorId(), ExternalUserRequest.builder().userId(1).dropFloor(5).pickupFloor(2).build()));

    }
}
