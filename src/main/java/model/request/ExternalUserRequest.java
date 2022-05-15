package model.request;

import lombok.Builder;
import lombok.Data;

@Data
public class ExternalUserRequest extends Request{
    private int userId;
    private int pickupFloor;
    private int dropFloor;

    @Builder(builderClassName = "childBuilder")
    public ExternalUserRequest(final int userId, final int pickupFloor, final int dropFloor) {
        super(Type.EXTERNAL_REQUEST);
        this.dropFloor = dropFloor;
        this.pickupFloor = pickupFloor;
        this.userId = userId;
    }
}
