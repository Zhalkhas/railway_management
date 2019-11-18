package org.yoptascript.inc.api;


import org.yoptascript.inc.certs.KeysReader;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/api")
public class ApiApp extends Application {
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();

    public ApiApp() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeysReader keysReader = new KeysReader("pub.der", "priv.der");
        KeyPair keyPair;
        try {
            keyPair = new KeyPair(keysReader.getPublicKey(), keysReader.getPrivateKey());
            System.out.println("successfully added keys");
        } catch (Exception e) {
            System.out.println("cannot set keypair");
            System.out.println(e.toString());
            keyPair = new KeyPair(keysReader.getPublicKey(),keysReader.getPrivateKey());
            e.printStackTrace();
//            Log.println("cannot set keypair");
//            Log.println(e.toString());
        }
        singletons.add(new AuthFilter());
        singletons.add(new Auth(keyPair));
        //singletons.add(new AuthFilter());
        singletons.add(new Ticket());
        singletons.add(new Search());
        singletons.add(new User());
        singletons.add(new Manager());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return empty;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}