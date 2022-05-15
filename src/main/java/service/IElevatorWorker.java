package service;

import model.request.Request;

public interface IElevatorWorker {
    void run();

    void addUser(final Request request);

}
