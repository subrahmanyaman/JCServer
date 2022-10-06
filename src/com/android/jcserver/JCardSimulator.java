package com.android.jcserver;

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

// NOTE: for jcop simulator, commented out below line
import com.android.javacard.keymaster.KMJCardSimApplet;
import com.licel.jcardsim.smartcardio.CardSimulator;
import com.licel.jcardsim.utils.AIDUtil;

import javacard.framework.AID;

public class JCardSimulator implements Simulator {

    private CardSimulator simulator;
    ResponseAPDU response;

    public JCardSimulator() {
    }

    @Override
    public void initaliseSimulator() throws Exception {
        // Create simulator
        simulator = new CardSimulator();
    }

    @Override
    public void disconnectSimulator() throws Exception {
        AID appletAID1 = AIDUtil.create("A000000062");
        simulator.deleteApplet(appletAID1);
    }

    private void installKeymaster() throws JCOPException {
        AID appletAID1 = AIDUtil.create("A000000062");
        // NOTE: for jcop simulator commented out below line
        simulator.installApplet(appletAID1, KMJCardSimApplet.class);
    }

    private void installFira() throws JCOPException {
    }

    @Override
    public void setupSimulator(String target) throws Exception {
        if (target.equals("keymaster")) {
            installKeymaster();
        } else if (target.equals("fira")) {
            installFira();
        }
        // Select applet
        // simulator.selectApplet(appletAID1);
    }

    private final byte[] intToByteArray(int value) {
        return new byte[] { (byte) (value >>> 8), (byte) value };
    }

    @Override
    public byte[] executeApdu(byte[] apdu) throws Exception {
        System.out.println("Executing APDU = " + Utils.byteArrayToHexString(apdu));
        CommandAPDU apduCmd = new CommandAPDU(apdu);
        response = simulator.transmitCommand(apduCmd);
        System.out.println(
                "Status = " + Utils.byteArrayToHexString(intToByteArray(response.getSW())));
        return intToByteArray(response.getSW());
    }

    @Override
    public byte[] decodeDataOut() {
        return response.getData();
    }

}
