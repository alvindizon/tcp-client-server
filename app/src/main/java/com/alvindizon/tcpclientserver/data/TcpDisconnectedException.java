package com.alvindizon.tcpclientserver.data;

import java.io.IOException;

public class TcpDisconnectedException extends IOException {

    private static final String MESSAGE = "TCP Client disconnected.";

    public TcpDisconnectedException() {
        super(MESSAGE);
    }
}
