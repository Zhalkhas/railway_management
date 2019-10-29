package org.yoptascript.inc.certs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;

public class KeysReader {
    private String pubname;
    private String privname;

    public KeysReader(String pubname, String privname) {
        this.privname = privname;
        this.pubname = pubname;
    }

    public PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String projPath = "";
        File file = new File(KeysReader.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        projPath = file.getPath();
        System.out.println(projPath);
        byte[] keyBytes = Files.readAllBytes(Paths.get(projPath+'/'+pubname));
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        String projPath = "";
        File file = new File(KeysReader.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        projPath = file.getPath();
        System.out.println(projPath);
        byte[] keyBytes = Files.readAllBytes(Paths.get(projPath+'/'+privname));
        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
