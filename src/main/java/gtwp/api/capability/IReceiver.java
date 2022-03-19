package gtwp.api.capability;

import java.util.UUID;

public interface IReceiver extends IAddresable {

    boolean setConnection(UUID netAddress);

    boolean isConnected();
}
