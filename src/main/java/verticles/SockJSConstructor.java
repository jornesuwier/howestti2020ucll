package verticles;

import config.Config;
import io.vertx.core.Vertx;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

class SockJSConstructor {

    private final SockJSHandler sockJSHandler;

    SockJSConstructor(final Vertx vertx) {
        sockJSHandler = SockJSHandler.create(vertx);
    }

    private void addBridgeOptions() {
        final PermittedOptions inbound = new PermittedOptions().setAddressRegex(Config.SOCKJS_URL);
        final BridgeOptions options = new BridgeOptions()
                .addInboundPermitted(inbound)
                .addOutboundPermitted(inbound);
        sockJSHandler.bridge(options);
    }

    SockJSHandler create() {
        addBridgeOptions();
        return sockJSHandler;
    }
}
