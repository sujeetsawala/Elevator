package resource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import model.elevator.Elevator;
import model.request.ExternalUserRequest;
import service.ElevatorService;

@Singleton
@Slf4j
public class ElevatorResource {
    private final ElevatorService elevatorService;

    @Inject
    public ElevatorResource(final ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
    }

    public Elevator addElevator(final int noOfFloors) {
        final Elevator elevator =  this.elevatorService.addElevators(noOfFloors);
        System.out.println(("Elevator created: " + elevator + " by thread: " + Thread.currentThread().getId()));
        return elevator;
    }

    public void getElevator(final int elevatorId, final ExternalUserRequest request) {
        try {
            this.elevatorService.getElevator(elevatorId, request);
           // System.out.println("User added: " + request + " by thread: " + Thread.currentThread().getId());
        } catch (Exception e) {
            System.out.println("Error adding users to elevator: " + e.getLocalizedMessage());
        }
    }
}
