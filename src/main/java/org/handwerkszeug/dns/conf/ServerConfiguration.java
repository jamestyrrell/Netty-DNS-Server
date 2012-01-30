package org.handwerkszeug.dns.conf;

import java.net.SocketAddress;
import java.util.Set;

public interface ServerConfiguration {

    Set<SocketAddress> getBindingHosts();

    int getThreadPoolSize();

    Set<SocketAddress> getForwarders();

    void setThreadPoolSize(int threadPoolSize);
}
