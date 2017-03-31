package org.jembi.mynfc.nfcUtils;

import java.io.IOException;

//todo think of a better name
public class NfcToken {

    private NfcReadEvent event;

    public NfcToken(NfcReadEvent event) {
        this.event = event;
    }

    public void write(String data) throws IOException {
        event.onReadComplete(data);
    }
}
