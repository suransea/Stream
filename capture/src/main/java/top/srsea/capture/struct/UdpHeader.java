package top.srsea.capture.struct;

import androidx.annotation.NonNull;

import java.util.Locale;

import top.srsea.capture.core.util.net.Packets;

public class UdpHeader {
    static final short offset_src_port = 0; // 源端口
    static final short offset_dest_port = 2; //目的端口
    static final short offset_tlen = 4; //数据报长度
    static final short offset_crc = 6; //校验和

    public byte[] data;
    public int offset;

    public UdpHeader(byte[] data, int offset) {
        this.data = data;
        this.offset = offset;
    }

    public short getSourcePort() {
        return Packets.readShort(data, offset + offset_src_port);
    }

    public void setSourcePort(short value) {
        Packets.writeShort(data, offset + offset_src_port, value);
    }

    public short getDestinationPort() {
        return Packets.readShort(data, offset + offset_dest_port);
    }

    public void setDestinationPort(short value) {
        Packets.writeShort(data, offset + offset_dest_port, value);
    }

    public int getTotalLength() {
        return Packets.readShort(data, offset + offset_tlen) & 0xFFFF;
    }

    public void setTotalLength(int value) {
        Packets.writeShort(data, offset + offset_tlen, (short) value);
    }

    public short getCrc() {
        return Packets.readShort(data, offset + offset_crc);
    }

    public void setCrc(short value) {
        Packets.writeShort(data, offset + offset_crc, value);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%d->%d", getSourcePort() & 0xFFFF, getDestinationPort() & 0xFFFF);
    }
}
