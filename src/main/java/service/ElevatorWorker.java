package service;

import lombok.extern.slf4j.Slf4j;
import model.elevator.Direction;
import model.elevator.Elevator;
import model.request.ExternalUserRequest;
import model.request.Request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ElevatorWorker implements IElevatorWorker {
    private final Elevator elevator;
    private volatile AtomicInteger maxFloor;
    private volatile AtomicInteger currFloor;
    private volatile AtomicInteger minFloor;
    private volatile Direction direction;
    private final Set<Integer> pickedUsers;
    private final ConcurrentMap<Integer, List<Request>> pickUpMap;
    private final ConcurrentMap<Integer, List<Integer>> dropMap;

    public ElevatorWorker(final Elevator elevator) {
        this.elevator = elevator;
        this.pickedUsers = new HashSet<>();
        this.maxFloor = new AtomicInteger(elevator.getNoOfFloors());
        this.minFloor = new AtomicInteger(0);
        this.currFloor = new AtomicInteger(0);
        this.direction = Direction.IDLE;

        this.pickUpMap = new ConcurrentHashMap<>();
        this.dropMap = new ConcurrentHashMap<>();

    }

    @Override
    public void run() {
        synchronized (this) {
            System.out.println("Elevator worker for elevatorId: " + elevator.getElevatorId() + " started");
            do {
                try {
                    if (pickUpMap.isEmpty() && dropMap.isEmpty()) {
                        this.direction = Direction.IDLE;
                        this.wait();
                    }
                } catch (Exception e) {
                    System.out.println("Error while thread wait: " + e.getLocalizedMessage());
                }

                if(this.direction.equals(Direction.IDLE)) {
                    if (this.currFloor.get() >= this.maxFloor.get()) {
                        this.direction = Direction.DOWN;
                    } else {
                        this.direction = Direction.UP;
                    }
                }
                else if (this.currFloor.get() >= this.maxFloor.get()) {
                    this.direction = Direction.DOWN;
                }
                else if(this.currFloor.get() <= this.minFloor.get()){
                    this.direction = Direction.UP;
                }

               // System.out.println("Max floor: " + this.maxFloor.get() + " Min floor: " + this.minFloor.get() + " Current floor " + this.currFloor.get() + " for elevator " + elevator.getElevatorId());
                this.compute();
                if (this.direction.equals(Direction.UP)) {
                    this.currFloor.compareAndSet(currFloor.get(), currFloor.get() + 1);
                } else {
                    this.currFloor.compareAndSet(currFloor.get(), currFloor.get() - 1);
                }
            } while (true);
        }
    }

    @Override
    public void addUser(final Request request) {
        synchronized (this) {
            final int dropFloor = ((ExternalUserRequest)request).getDropFloor();
            final int userId = ((ExternalUserRequest)request).getUserId();
            final int pickupFloor = ((ExternalUserRequest)request).getPickupFloor();

            if(this.pickedUsers.contains(userId)) {
                System.out.println("User already in the elevator");
                return;
            }

            if(pickUpMap.containsKey(pickupFloor)) {
                pickUpMap.get(pickupFloor).add(request);
            }
            else {
                final List<Request> users = new ArrayList<>();
                users.add(request);
                pickUpMap.put(pickupFloor, users);
            }

            System.out.println("User added to elevator: " + elevator.getElevatorId() + " " + request + " by thread: " + Thread.currentThread().getId() + " current floor: " + currFloor);

            final Integer maxFloor = Math.max(this.maxFloor.get(), Math.max(dropFloor, pickupFloor));
            final Integer minFloor = Math.min(this.minFloor.get(), Math.min(dropFloor, pickupFloor));

            this.maxFloor.compareAndSet(this.maxFloor.get(), maxFloor);
            this.minFloor.compareAndSet(this.minFloor.get(), minFloor);
            this.notify();
        }
    }

    private void compute() {
        final List<Request> pickUpUsers = this.pickUpMap.get(currFloor.get());
        if(pickUpUsers != null) {
            for(Request user: pickUpUsers) {
                if(!pickedUsers.contains(((ExternalUserRequest)user).getUserId())) {
                    System.out.println("User: " + ((ExternalUserRequest)user).getUserId() + " entered the elevator " + elevator.getElevatorId() + " via thread: " + Thread.currentThread().getId());
                    this.pickedUsers.add(((ExternalUserRequest)user).getUserId());
                    if(this.dropMap.containsKey(((ExternalUserRequest)user))) {
                        this.dropMap.get(((ExternalUserRequest)user).getDropFloor()).add(((ExternalUserRequest)user).getUserId());
                    } else {
                        final List<Integer> users = new ArrayList<>();
                        users.add(((ExternalUserRequest) user).getUserId());
                        this.dropMap.put(((ExternalUserRequest)user).getDropFloor(), users);
                    }
                }
            }
        }


        final List<Integer> dropUsers = this.dropMap.get(currFloor.get());
        if(dropUsers != null) {
            for(Integer user: dropUsers) {
                if(pickedUsers != null  && pickedUsers.contains(user)) {
                    System.out.println("User: " + user + " exited the elevator " + elevator.getElevatorId() + " via thread: " + Thread.currentThread().getId());
                    this.pickedUsers.remove(user);
                }
            }
        }

        if(this.pickUpMap.containsKey(currFloor.get())) {
            this.pickUpMap.remove(currFloor.get());
        }

        if(this.dropMap.containsKey(currFloor.get())) {
            this.dropMap.remove(currFloor.get());
        }
    }
}
