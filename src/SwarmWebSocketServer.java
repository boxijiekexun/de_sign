import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier; // 1. 导入 Supplier 接口

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

public class SwarmWebSocketServer extends WebSocketServer {

    private final Set<WebSocket> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    // 2. 新增一个数据提供者接口
    private final Supplier<String> stateSupplier;

    // 3. 修改构造函数，接收 stateSupplier
    public SwarmWebSocketServer(int port, Supplier<String> stateSupplier) {
        super(new InetSocketAddress(port));
        this.stateSupplier = stateSupplier; 
        System.out.println("WebSocket Server 启动在端口: " + port);
    }

    @Override
    public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
        connections.add(conn);
        System.out.println("新的前端连接加入: " + conn.getRemoteSocketAddress());
        
        // 4. 【核心优化】一连接上，立刻调用接口获取最新数据并发送！
        if (stateSupplier != null) {
            String currentState = stateSupplier.get();
            if (currentState != null) {
                conn.send(currentState);
                System.out.println(">> 已向新连接补发最新状态数据");
            }
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        System.out.println("前端连接断开: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("收到前端指令: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server 成功启动!");
        setConnectionLostTimeout(0);
    }

    public void broadcast(String message) {
        for (WebSocket conn : connections) {
            conn.send(message);
        }
    }
}
