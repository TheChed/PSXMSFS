// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.wrappers;

import java.nio.ByteBuffer;
import flightsim.simconnect.Messages;
import flightsim.simconnect.data.SimConnectData;

public class GUID implements SimConnectData, Comparable<GUID>
{
    protected final byte[] data;
    
    public GUID() {
        this.data = new byte[16];
    }
    
    public GUID(final byte[] data) {
        this(data, 0);
    }
    
    public GUID(final byte[] data, final int offset) {
        System.arraycopy(data, offset, this.data = new byte[16], 0, 16);
    }
    
    public GUID(final int arg1, final short arg2, final short arg3, final byte[] arg4) throws IllegalArgumentException {
        this.data = decode(arg1, arg2, arg3, arg4);
    }
    
    public GUID(final String s) {
        this.data = decodeRegistry(s);
    }
    
    public static byte[] decodeRegistry(String s) {
        s = s.trim().toLowerCase().substring(1, s.length() - 1);
        if (s.length() != 36) {
            throw new IllegalArgumentException(Messages.getString("GUID.0"));
        }
        final String[] parts = s.split("-");
        if (parts.length != 5) {
            throw new IllegalArgumentException(Messages.getString("GUID.2"));
        }
        final byte[] data = new byte[16];
        try {
            int a = (int)(Long.parseLong(parts[0], 16) & -1L);
            data[0] = (byte)(a & 0xFF);
            data[1] = (byte)(a >> 8 & 0xFF);
            data[2] = (byte)(a >> 16 & 0xFF);
            data[3] = (byte)(a >> 24 & 0xFF);
            a = Integer.parseInt(parts[1], 16);
            data[4] = (byte)(a & 0xFF);
            data[5] = (byte)(a >> 8 & 0xFF);
            a = Integer.parseInt(parts[2], 16);
            data[6] = (byte)(a & 0xFF);
            data[7] = (byte)(a >> 8 & 0xFF);
            a = Integer.parseInt(parts[3], 16);
            data[8] = (byte)(a >> 8 & 0xFF);
            data[9] = (byte)(a & 0xFF);
            for (int i = 0, j = 10; i < parts[4].length(); i += 2, ++j) {
                data[j] = (byte)(Integer.parseInt(parts[4].substring(i, i + 2), 16) & 0xFF);
            }
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(nfe);
        }
        return data;
    }
    
    public static byte[] decodeOldRegistry(final String s) {
        final byte[] data = new byte[16];
        for (int i = 0; i < 4; ++i) {
            final int v = (int)(Long.parseLong(s.substring(8 * i, 8 * i + 8), 16) & -1L);
            data[4 * i + 3] = (byte)(v >> 24 & 0xFF);
            data[4 * i + 2] = (byte)(v >> 16 & 0xFF);
            data[4 * i + 1] = (byte)(v >> 8 & 0xFF);
            data[4 * i] = (byte)(v & 0xFF);
        }
        return data;
    }
    
    public static byte[] decode(final int arg1, final short arg2, final short arg3, final byte[] arg4) {
        if (arg4 == null || arg4.length != 8) {
            throw new IllegalArgumentException(Messages.getString("GUID.3"));
        }
        final byte[] data = new byte[16];
        data[3] = (byte)(arg1 >> 24 & 0xFF);
        data[2] = (byte)(arg1 >> 16 & 0xFF);
        data[1] = (byte)(arg1 >> 8 & 0xFF);
        data[0] = (byte)(arg1 & 0xFF);
        data[5] = (byte)(arg2 >> 8 & 0xFF);
        data[4] = (byte)(arg2 & 0xFF);
        data[7] = (byte)(arg3 >> 8 & 0xFF);
        data[6] = (byte)(arg3 & 0xFF);
        System.arraycopy(arg4, 0, data, 8, 8);
        return data;
    }
    
    public static GUID parseGUID(final String s) {
        return new GUID(s);
    }
    
    @Override
    public String toString() {
        final StringBuffer sgb = new StringBuffer("");
        sgb.append('{');
        if ((this.data[3] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[3] & 0xFF));
        if ((this.data[2] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[2] & 0xFF));
        if ((this.data[1] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[1] & 0xFF));
        if ((this.data[0] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[0] & 0xFF));
        sgb.append('-');
        if ((this.data[5] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[5] & 0xFF));
        if ((this.data[4] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[4] & 0xFF));
        sgb.append('-');
        if ((this.data[7] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[7] & 0xFF));
        if ((this.data[6] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[6] & 0xFF));
        sgb.append('-');
        if ((this.data[8] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[8] & 0xFF));
        if ((this.data[9] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[9] & 0xFF));
        sgb.append('-');
        if ((this.data[10] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[10] & 0xFF));
        if ((this.data[11] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[11] & 0xFF));
        if ((this.data[12] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[12] & 0xFF));
        if ((this.data[13] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[13] & 0xFF));
        if ((this.data[14] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[14] & 0xFF));
        if ((this.data[15] & 0xFF) < 16) {
            sgb.append('0');
        }
        sgb.append(Integer.toHexString(this.data[15] & 0xFF));
        sgb.append('}');
        return sgb.toString().toUpperCase();
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        for (int i = 0; i < 16; ++i) {
            h = 31 * h + this.data[i];
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof GUID) {
            final GUID g = (GUID)obj;
            for (int i = 0; i < 16; ++i) {
                if (this.data[i] != g.data[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int compareTo(final GUID g) {
        return this.toString().compareToIgnoreCase(g.toString());
    }
    
    @Override
    public void read(final ByteBuffer buffer) {
        buffer.get(this.data);
    }
    
    @Override
    public void write(final ByteBuffer buffer) {
        buffer.put(this.data);
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public static void main(final String[] args) {
        final GUID g = new GUID(decodeOldRegistry("384058AD4BCA29522AF62095A6DB2206"));
        System.out.println(g);
        final GUID g2 = new GUID("{384058ad-2952-4bca-9520-f62a0622dba6}");
        System.out.println(g2);
        System.out.println(g.equals(g2));
        for (int i = 0; i < 16; ++i) {
            System.out.println(Integer.toHexString(g.data[i] & 0xFF));
        }
    }
}
