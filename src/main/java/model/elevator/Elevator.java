package model.elevator;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Elevator {
    private int elevatorId;
    private int noOfFloors;
}
