package de.klierlinge.partydjremote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public final class WakeOnLan {
    private static final int PORT = 9;
    private static final Pattern SPLIT_MAC_PATTERN = Pattern.compile("(:|\\-)");

    private WakeOnLan() {
    }

    public static void WakeUp(String ipStr, CharSequence macStr, SuccessListener onSuccess, ErrorListener onError) {
        final byte[] macBytes = getMacBytes(macStr);
        final byte[] bytes = new byte[6 + 16 * macBytes.length];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }
        for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                final InetAddress address = InetAddress.getByName(ipStr);
                final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
                socket.send(packet);
                socket.close();
            } catch (IOException e) {
                if(onError != null)
                    onError.error(e);
            }
            if(onSuccess != null)
                onSuccess.successfull();
        }).start();
    }

    private static byte[] getMacBytes(CharSequence macStr) throws IllegalArgumentException {
        final String[] hex = SPLIT_MAC_PATTERN.split(macStr);
        if (hex.length != 6) {
            throw new IllegalArgumentException("MAC address must have 6 blocks split with ':'.");
        }

        final byte[] bytes = new byte[6];
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.", e);
        }
        return bytes;
    }

    @FunctionalInterface
    public interface SuccessListener {
        void successfull();
    }

    @FunctionalInterface
    public interface ErrorListener {
        void error(Exception exception);
    }
}
