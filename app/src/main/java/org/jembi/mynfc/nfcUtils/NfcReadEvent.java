package org.jembi.mynfc.nfcUtils;

/**
 * Created by barry on 2017/03/24.
 */

public interface NfcReadEvent {
    void onReadComplete(String data);
}
