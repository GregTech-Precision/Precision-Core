package gtwp.api.capability;

import java.util.UUID;

public interface IReceiver extends IConnectible {

    boolean setConnection(UUID netAddress);

    boolean isConnected();

    void breakConnection();
}
