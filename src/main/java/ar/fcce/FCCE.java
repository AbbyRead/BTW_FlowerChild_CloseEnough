package ar.fcce;

import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class FCCE {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE = Null.get();

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();
}
