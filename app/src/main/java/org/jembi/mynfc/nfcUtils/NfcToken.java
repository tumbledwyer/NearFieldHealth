package org.jembi.mynfc.nfcUtils;

import org.jembi.mynfc.nfcUtils.NfcReadEvent;

/**
 * Created by barry on 2017/03/24.
 */

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