!function (e) {
    if ("function" == typeof require && "undefined" != typeof module) {
        var n = require("sockjs-client");
        if (!n) throw new Error("vertx-eventbus.js requires sockjs-client, see http://sockjs.org");
        e(n)
    } else if ("function" == typeof define && define.amd) define("vertx-eventbus", ["sockjs"], e); else {
        if (void 0 === this.SockJS) throw new Error("vertx-eventbus.js requires sockjs-client, see http://sockjs.org");
        EventBus = e(this.SockJS)
    }
}(function (r) {
    function i(e, n) {
        if (e) {
            if (!n) return e;
            for (var t in e) e.hasOwnProperty(t) && void 0 === n[t] && (n[t] = e[t])
        }
        return n || {}
    }

    var a = function (e, n) {
        var s = this;
        n = n || {}, this.pingInterval = n.vertxbus_ping_interval || 5e3, this.pingTimerID = null, this.reconnectEnabled = !1, this.reconnectAttempts = 0, this.reconnectTimerID = null, this.maxReconnectAttempts = n.vertxbus_reconnect_attempts_max || 1 / 0, this.reconnectDelayMin = n.vertxbus_reconnect_delay_min || 1e3, this.reconnectDelayMax = n.vertxbus_reconnect_delay_max || 5e3, this.reconnectExponent = n.vertxbus_reconnect_exponent || 2, this.randomizationFactor = n.vertxbus_randomization_factor || .5;
        this.defaultHeaders = null, this.onerror = function (e) {
            try {
                console.error(e)
            } catch (e) {
            }
        };
        var t = function () {
            s.sockJSConn = new r(e, null, n), s.state = a.CONNECTING, s.handlers = {}, s.replyHandlers = {}, s.sockJSConn.onopen = function () {
                s.enablePing(!0), s.state = a.OPEN, s.onopen && s.onopen(), s.reconnectTimerID && (s.reconnectAttempts = 0, s.onreconnect && s.onreconnect())
            }, s.sockJSConn.onclose = function (e) {
                s.state = a.CLOSED, s.pingTimerID && clearInterval(s.pingTimerID), s.reconnectEnabled && s.reconnectAttempts < s.maxReconnectAttempts && (s.sockJSConn = null, s.reconnectTimerID = setTimeout(t, function () {
                    var e = s.reconnectDelayMin * Math.pow(s.reconnectExponent, s.reconnectAttempts);
                    if (s.randomizationFactor) {
                        var n = Math.random(), t = Math.floor(n * s.randomizationFactor * e);
                        e = 0 == (1 & Math.floor(10 * n)) ? e - t : e + t
                    }
                    return 0 | Math.min(e, s.reconnectDelayMax)
                }()), ++s.reconnectAttempts), s.onclose && s.onclose(e)
            }, s.sockJSConn.onmessage = function (e) {
                var r = JSON.parse(e.data);
                if (r.replyAddress && Object.defineProperty(r, "reply", {
                        value: function (e, n, t) {
                            s.send(r.replyAddress, e, n, t)
                        }
                    }), s.handlers[r.address]) for (var n = s.handlers[r.address], t = 0; t < n.length; t++) "err" === r.type ? n[t]({
                    failureCode: r.failureCode,
                    failureType: r.failureType,
                    message: r.message
                }) : n[t](null, r); else if (s.replyHandlers[r.address]) {
                    var o = s.replyHandlers[r.address];
                    delete s.replyHandlers[r.address], "err" === r.type ? o({
                        failureCode: r.failureCode,
                        failureType: r.failureType,
                        message: r.message
                    }) : o(null, r)
                } else if ("err" === r.type) s.onerror(r); else try {
                    console.warn("No handler found for message: ", r)
                } catch (e) {
                }
            }
        };
        t()
    };
    if (a.prototype.send = function (e, n, t, r) {
            if (this.state != a.OPEN) throw new Error("INVALID_STATE_ERR");
            "function" == typeof t && (r = t, t = {});
            var o = {type: "send", address: e, headers: i(this.defaultHeaders, t), body: n};
            if (r) {
                var s = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (e, n) {
                    return n = 16 * Math.random(), ("y" == e ? 3 & n | 8 : 0 | n).toString(16)
                });
                o.replyAddress = s, this.replyHandlers[s] = r
            }
            this.sockJSConn.send(JSON.stringify(o))
        }, a.prototype.publish = function (e, n, t) {
            if (this.state != a.OPEN) throw new Error("INVALID_STATE_ERR");
            this.sockJSConn.send(JSON.stringify({
                type: "publish",
                address: e,
                headers: i(this.defaultHeaders, t),
                body: n
            }))
        }, a.prototype.registerHandler = function (e, n, t) {
            if (this.state != a.OPEN) throw new Error("INVALID_STATE_ERR");
            "function" == typeof n && (t = n, n = {}), this.handlers[e] || (this.handlers[e] = [], this.sockJSConn.send(JSON.stringify({
                type: "register",
                address: e,
                headers: i(this.defaultHeaders, n)
            }))), this.handlers[e].push(t)
        }, a.prototype.unregisterHandler = function (e, n, t) {
            if (this.state != a.OPEN) throw new Error("INVALID_STATE_ERR");
            var r = this.handlers[e];
            if (r) {
                "function" == typeof n && (t = n, n = {});
                var o = r.indexOf(t);
                -1 != o && (r.splice(o, 1), 0 === r.length && (this.sockJSConn.send(JSON.stringify({
                    type: "unregister",
                    address: e,
                    headers: i(this.defaultHeaders, n)
                })), delete this.handlers[e]))
            }
        }, a.prototype.close = function () {
            this.state = a.CLOSING, this.enableReconnect(!1), this.sockJSConn.close()
        }, a.CONNECTING = 0, a.OPEN = 1, a.CLOSING = 2, a.CLOSED = 3, a.prototype.enablePing = function (e) {
            var n = this;
            if (e) {
                var t = function () {
                    n.sockJSConn.send(JSON.stringify({type: "ping"}))
                };
                0 < n.pingInterval && (t(), n.pingTimerID = setInterval(t, n.pingInterval))
            } else n.pingTimerID && (clearInterval(n.pingTimerID), n.pingTimerID = null)
        }, a.prototype.enableReconnect = function (e) {
            var n = this;
            !(n.reconnectEnabled = e) && n.reconnectTimerID && (clearTimeout(n.reconnectTimerID), n.reconnectTimerID = null, n.reconnectAttempts = 0)
        }, "undefined" == typeof exports) return a;
    "undefined" != typeof module && module.exports ? exports = module.exports = a : exports.EventBus = a
});