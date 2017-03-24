package org.jembi.mynfc.nfcUtils;

//todo think of a better name
public class NfcToken {

    private NfcReadEvent event;

    public NfcToken(NfcReadEvent event) {
        this.event = event;
    }

    public void write(String data){
        event.onReadComplete(data);
    }
}
