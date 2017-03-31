package org.jembi.nearFieldHealth.nfcUtils;

import java.io.IOException;

/**
 * Created by barry on 2017/03/24.
 */

public interface NfcReadEvent {
    void onReadComplete(String data) throws IOException;
}
