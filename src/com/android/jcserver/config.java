package com.android.jcserver;

import java.io.File;

public class config {

    public static final String JCOP_PROVIDER = "jcop";
    public static final String JCARDSIM_PROVIDER = "jcardsim";
    public static final int PORT = 8080;

    public static final byte[] KEYMASTER_PKG_AID = 
            Utils.hexStringToByteArray("A00000006203020C0101");
    public static final byte[] KEYMASTER_AID = 
            Utils.hexStringToByteArray("A00000006203020C010101");
    public static final byte[] SEPROVIDER_PKG_AID = 
            Utils.hexStringToByteArray("A00000006203020C0102");

    public static final String CAP_SEPRIVIDER = "ExtBinaries/seprovider.cap";
    public static final String CAP_KEYMASTER = "ExtBinaries/keymaster.cap";

    public static String getAbsolutePath(String path) {
        File file = new File(path);
        return file.getAbsolutePath();
    }
}
