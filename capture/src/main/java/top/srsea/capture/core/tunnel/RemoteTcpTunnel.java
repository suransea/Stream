package top.srsea.capture.core.tunnel;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;

import top.srsea.capture.core.nat.NatSessionManager;
import top.srsea.capture.core.nat.NatSession;
import top.srsea.capture.core.util.common.ACache;
import top.srsea.capture.core.util.android.PortHostService;
import top.srsea.capture.core.util.net.TcpDataSaver;
import top.srsea.capture.core.util.common.ThreadPool;
import top.srsea.capture.core.util.common.TimeFormatter;

public class RemoteTcpTunnel extends RawTcpTunnel {
    private final Handler handler;
    TcpDataSaver helper;
    NatSession session;

    public RemoteTcpTunnel(InetSocketAddress serverAddress, Selector selector, short portKey) throws IOException {
        super(serverAddress, selector, portKey);
        session = NatSessionManager.getSession(portKey);
        String dir = new StringBuilder()
                .append(TcpDataSaver.DATA_DIR)
                .append(TimeFormatter.formatToYYMMDDHHMMSS(session.vpnStartTime))
                .append("/")
                .append(session.getUniqueName())
                .toString();
        helper = new TcpDataSaver(dir);
        handler = new Handler(Looper.getMainLooper());

    }


    @Override
    protected void afterReceived(ByteBuffer buffer) throws Exception {
        super.afterReceived(buffer);
        refreshSessionAfterRead(buffer.limit());
        TcpDataSaver.TcpData saveTcpData = new TcpDataSaver.TcpData
                .Builder()
                .isRequest(false)
                .needParseData(buffer.array())
                .length(buffer.limit())
                .offSet(0)
                .build();
        helper.addData(saveTcpData);

    }

    @Override
    protected void beforeSend(ByteBuffer buffer) throws Exception {
        super.beforeSend(buffer);
        TcpDataSaver.TcpData saveTcpData = new TcpDataSaver.TcpData
                .Builder()
                .isRequest(true)
                .needParseData(buffer.array())
                .length(buffer.limit())
                .offSet(0)
                .build();
        helper.addData(saveTcpData);
        refreshAppInfo();

    }

    private void refreshAppInfo() {
        if (session.appInfo != null) {
            return;
        }
        if (PortHostService.getInstance() != null) {
            ThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    PortHostService.getInstance().refreshSessionInfo();
                }
            });
        }
    }

    private void refreshSessionAfterRead(int size) {

        session.receivePacketNum++;
        session.receiveByteNum += size;

    }

    @Override
    protected void onDispose() {
        super.onDispose();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ThreadPool.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (session.receiveByteNum == 0 && session.bytesSent == 0) {
                            return;
                        }
                        String configFileDir = TcpDataSaver.CONFIG_DIR
                                + TimeFormatter.formatToYYMMDDHHMMSS(session.vpnStartTime);
                        File parentFile = new File(configFileDir);
                        if (!parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        File file = new File(parentFile, session.getUniqueName());
                        if (file.exists()) {
                            return;
                        }
                        ACache configACache = ACache.get(parentFile);
                        configACache.put(session.getUniqueName(), session);
                    }
                });
            }
        }, 1000);
    }
}
