package service;

import com.google.inject.Singleton;
import model.elevator.Elevator;
import model.request.ExternalUserRequest;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class ElevatorService {
    private final ConcurrentMap<Integer, ElevatorWorker> workerMap;
    private final ExecutorService[] executorServicesPool;
    private final int POOL_SIZE = 100;

    public ElevatorService() {
        this.workerMap = new ConcurrentHashMap<>();
        this.executorServicesPool = new ExecutorService[POOL_SIZE];
        for(int i = 0; i < POOL_SIZE; i++) {
            executorServicesPool[i] = Executors.newSingleThreadExecutor();
        }
    }

    public synchronized Elevator addElevators(final int noOfFloors) {
        final int elevatorId = Math.abs(UUID.randomUUID().hashCode());
        final Elevator elevator = Elevator.builder().elevatorId(elevatorId).noOfFloors(noOfFloors).build();
        final ElevatorWorker elevatorWorker = new ElevatorWorker(elevator);
        workerMap.put(elevatorId, elevatorWorker);
        executorServicesPool[elevatorId % POOL_SIZE].execute(() -> this.workerMap.get(elevatorId).run());
        return elevator;
    }

    public void getElevator(final int elevatorId, final ExternalUserRequest request) throws Exception {
        if(workerMap.containsKey(elevatorId)) {
            this.workerMap.get(elevatorId).addUser(request);
        }
        else {
            throw new Exception();
        }
    }
}
