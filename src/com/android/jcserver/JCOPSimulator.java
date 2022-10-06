package com.android.jcserver;

import static com.android.jcserver.config.*;

public class JCOPSimulator implements Simulator {

    private JCOPOpenCard openCardSim = null;
    private opencard.core.terminal.ResponseAPDU response;

    public JCOPSimulator() {
    }

    @Override
    public void initaliseSimulator() throws Exception {
        openCardSim = JCOPOpenCard.getInstance();
        if (!openCardSim.isConnected()) {
            try {
                openCardSim.connect();
            } catch (JCOPException e) {
                openCardSim.close();
                throw new JCOPException(e.getMessage());
            }
        }
    }

    @Override
    public void disconnectSimulator() throws Exception {
        openCardSim.deleteApplet(KEYMASTER_PKG_AID);
        openCardSim.close();
    }

    private void installKeymaster() throws JCOPException {
        openCardSim.installApplet(getAbsolutePath(CAP_SEPRIVIDER), null,
                SEPROVIDER_PKG_AID);
        openCardSim.installApplet(getAbsolutePath(CAP_KEYMASTER), KEYMASTER_AID,
                KEYMASTER_PKG_AID);
    }

    private void installFira() {   }

    @Override
    public void setupSimulator(String target) throws Exception {
        try {
            if (target.equals("keymaster")) {
                installKeymaster();
            } else if (target.equals("fira")) {
                installFira();
            } else {
                installKeymaster();
                installFira();
            }
        } catch (JCOPException e) {
            openCardSim.close();
            throw new JCOPException(e.getMessage());
        }
    }

    private final byte[] intToByteArray(int value) {
        return new byte[] { (byte) (value >>> 8), (byte) value };
    }

    private javax.smartcardio.CommandAPDU validateApdu(byte[] apdu)
            throws IllegalArgumentException {
        javax.smartcardio.CommandAPDU apduCmd = new javax.smartcardio.CommandAPDU(apdu);
        return apduCmd;
    }

    @Override
    public byte[] executeApdu(byte[] apdu) throws Exception {
        System.out.println("Executing APDU = " + Utils.byteArrayToHexString(apdu));
        if (null == validateApdu(apdu)) {
            throw new IllegalArgumentException();
        }
        opencard.core.terminal.CommandAPDU cmdApdu = new opencard.core.terminal.CommandAPDU(apdu);
        response = openCardSim.transmitCommand(cmdApdu);
        System.out.println("Status = " + Utils.byteArrayToHexString(intToByteArray(response.sw())));
        return intToByteArray(response.sw());
    }

    @Override
    public byte[] decodeDataOut() {
        // TODO Auto-generated method stub
        return response.getBytes();
    }

}
