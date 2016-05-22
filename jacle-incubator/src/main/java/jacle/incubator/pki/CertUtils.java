package jacle.incubator.pki;

import jacle.common.io.CloseablesExt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.EncryptedPrivateKeyInfo;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import sun.misc.BASE64Encoder;
import sun.security.provider.X509Factory;

public class CertUtils {

    // Ensure the "BC" provider is defined
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    /**
     * sStatic accessor.
     */
    public static final CertUtils I = new CertUtils();
    
    private static final long YEARS_3_MS = 1000L * 60 * 60 * 24 * 364 * 3;
    
    public KeyPair createKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096);
            return keyPairGenerator.generateKeyPair();
        } catch (RuntimeException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create public/private key pair", e);
        }
    }

    public X509Certificate createCaCertificate(KeyPair keyPair, X500Name subject, Date beginDate, Date endDate, int certSerial) {
        try {
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    subject,
                    BigInteger.valueOf(certSerial),
                    beginDate,
                    endDate,
                    subject,
                    keyPair.getPublic());
            
            BcX509ExtensionUtils extUtils = new BcX509ExtensionUtils();
            
            SubjectPublicKeyInfo subjPubKeyInfo = getSubjectPublicKeyInfo(keyPair);
            SubjectKeyIdentifier subjectKey = extUtils.createSubjectKeyIdentifier(subjPubKeyInfo);
            AuthorityKeyIdentifier authorityKey = extUtils.createAuthorityKeyIdentifier(subjPubKeyInfo);
            builder.addExtension(Extension.subjectKeyIdentifier, false, subjectKey);
            builder.addExtension(Extension.authorityKeyIdentifier, false, authorityKey);
            builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
            
            KeyUsage usage = new KeyUsage(
                    KeyUsage.keyCertSign | KeyUsage.digitalSignature | 
                    KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.cRLSign);
            builder.addExtension(Extension.keyUsage, false, usage);
            
            // TODO [rkenney]: Unify the signing code with the other bit. This uses JcaContentSignerBuilder. The other, BC.
            
            ContentSigner signer = new JcaContentSignerBuilder("SHA512WithRSAEncryption").setProvider("BC").build(keyPair.getPrivate());
            
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(builder.build(signer));
            
            cert.checkValidity(new Date());
            cert.verify(keyPair.getPublic());
    
            return cert;
            
        } catch (RuntimeException | IOException | OperatorCreationException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to create CA certificate", e);
        }
    }
    
    public PKCS10CertificationRequest createCSR(KeyPair keyPair, X500Name subject) {
        try {
            PKCS10CertificationRequestBuilder p10Builder =
                    new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
            JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA512withRSA");
            ContentSigner signer = csBuilder.build(keyPair.getPrivate());
            return p10Builder.build(signer);
        } catch (RuntimeException | OperatorCreationException e) {
            throw new RuntimeException("Failed to create CSR for subject ["+subject+"]", e);
        }
    }
    
    public CreateCertFromCSR createCertFromCSR() {
        return new CreateCertFromCSR(); 
    }

    public PrivateKey readPrivateKeyFromPem(File file) {
        FileReader reader = null;
        PEMParser parser = null;
        try {
            reader = new FileReader(file);
            parser = new PEMParser(reader);
            PrivateKeyInfo keyInfo = (PrivateKeyInfo) parser.readObject();
            return new JcaPEMKeyConverter().getPrivateKey(keyInfo);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("Failed to load private key from ["+file+"]", e);
        } finally {
        	CloseablesExt.closeQuietly(parser);
        	CloseablesExt.closeQuietly(reader);
        }
    }

    public PublicKey readPublicKeyFromPem(File file) {
        FileReader reader = null;
        PEMParser parser = null;
        try {
            reader = new FileReader(file);
            parser = new PEMParser(reader);
            SubjectPublicKeyInfo keyInfo = (SubjectPublicKeyInfo) parser.readObject();
            return new JcaPEMKeyConverter().getPublicKey(keyInfo);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("Failed to load public key from ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(parser);
            CloseablesExt.closeQuietly(reader);
        }
    }

    public PKCS10CertificationRequest readSigningRequestFromPem(File file) {
        FileReader reader = null;
        PEMParser parser = null;
        try {
            reader = new FileReader(file);
            parser = new PEMParser(reader);
            return (PKCS10CertificationRequest) parser.readObject();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("Failed to load CSR from ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(parser);
            CloseablesExt.closeQuietly(reader);
        }
    }

    public X509CertificateHolder readCertificateFromPem(File file) {
        FileReader reader = null;
        PEMParser parser = null;
        try {
            reader = new FileReader(file);
            parser = new PEMParser(reader);
            return (X509CertificateHolder) parser.readObject();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("Failed to load certficate from ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(parser);
            CloseablesExt.closeQuietly(reader);
        }
    }

    public KeyStore readKeyStore(File file, char[] password) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            KeyStore ks = KeyStore.getInstance("JKS");   
            ks.load(stream, password);
            return ks;
        } catch (RuntimeException | IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to read keystore from ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(stream);
        }
    }

    // TODO [rkenney]: Rename related methods to "Pem"
    public void writeToDerFile(PrivateKey key, File file) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            IOUtils.write(key.getEncoded(), stream);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("Failed to write key to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(stream);
        }
    }

    // TODO [rkenney]: Rename related methods to "Pem"
    public void writeToDerFile(X509Certificate cert, File file) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            IOUtils.write(cert.getEncoded(), stream);
        } catch (RuntimeException | IOException | CertificateEncodingException e) {
            throw new RuntimeException("Failed to write certificate to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(stream);
        }
    }

    public void writeToDerFile(EncryptedPrivateKeyInfo pkcs8, File file) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            IOUtils.write(pkcs8.getEncoded(), stream);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("Failed to write PKCS8 to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(stream);
        }
    }

    public void writeToPemFile(X509Certificate cert, File file) {
        OutputStream stream = null;
        PrintWriter writer = null;
        try {
            // TODO [rkenney]: Join this with writeToPemFile()
            stream = new FileOutputStream(file);
            writer = new PrintWriter(stream);
            BASE64Encoder encoder = new BASE64Encoder();
            writer.println(X509Factory.BEGIN_CERT);
            writer.flush();
            encoder.encodeBuffer(cert.getEncoded(), stream);
            writer.println(X509Factory.END_CERT);
            writer.flush();
        } catch (RuntimeException | IOException | CertificateEncodingException e) {
            throw new RuntimeException("Failed to write certificate to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(writer);
            CloseablesExt.closeQuietly(stream);
        }
    }

    public void writeToPemFile(PrivateKey key, File file) {
        writeToPemFile(PemType.PRIVATE_KEY, key.getEncoded(), file);
    }

    public void writeToPemFile(PublicKey key, File file) {
        writeToPemFile(PemType.PUBLIC_KEY, key.getEncoded(), file);
    }

    public void writeToPemFile(PKCS10CertificationRequest request, File file) {
        try {
            writeToPemFile(PemType.CERTIFICATE_REQUEST, request.getEncoded(), file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSR PEM to ["+file+"]", e);
        }
    }

    public void writeKeyStore(KeyStore keystore, char[] password, File file) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            keystore.store(stream, password);
        } catch (RuntimeException | IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to write keystore to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(stream);
        }
    }

    public void writeToP12(X509Certificate cert, PrivateKey key, char[] password, File file) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            KeyStore ks = KeyStore.getInstance("PKCS12", "BC");   
            ks.load(null,null);
            ks.setCertificateEntry(cert.getSerialNumber().toString(), cert);
            ks.setKeyEntry(cert.getSerialNumber().toString(), key, password, new java.security.cert.Certificate[]{cert,cert});
            ks.store(stream, password);
        } catch (RuntimeException | IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to write PKCS12 to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(stream);
        }
    }
    
    public void writeToPemFile(PemType pemType, byte[] bytes, File file) {
        PrintWriter writer = null;
        PemWriter pemWriter = null;
        try {
            writer = new PrintWriter(file);
            PemObject pemObject = new PemObject(pemType.value, bytes);
            pemWriter = new PemWriter(writer);
            pemWriter.writeObject(pemObject);
            pemWriter.close();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("Failed to write PEM content to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(pemWriter);
            CloseablesExt.closeQuietly(writer);
        }
    }

    private static SubjectPublicKeyInfo getSubjectPublicKeyInfo(KeyPair keyPair) {
        return new SubjectPublicKeyInfo(
                ASN1Sequence.getInstance(keyPair.getPublic().getEncoded()));
    }

    /**
     * Used to sign CSR, resulting in an X509 certificate. This is an
     * object instead of a method due to the number of arguments.
     */
    public static class CreateCertFromCSR {

        private PKCS10CertificationRequest request;
        private PublicKey requesterPubKey;
        private X500Name caSubject;
        private PrivateKey caKey;
        private Integer certSerial;
        private Date beginDate = new Date();
        private Date endDate = new Date(beginDate.getTime() + YEARS_3_MS);

        /**
         * Setter for the CSR and requester's public key.
         * 
         * @param csr
         *            The signing request to sign
         * @param requesterKey
         *            The requester's public key
         * 
         * @return This object (fluent setter)
         */
        public CreateCertFromCSR setRequestInfo(PKCS10CertificationRequest csr, PublicKey requesterKey) {
            request = csr;
            requesterPubKey = requesterKey;
            return this;
        }
        
        /**
         * Setter for the CA subject and private key.
         * 
         * @param subject
         *            The CA's subject (signer's subject).
         * @param key
         *            The CA's private key.
         * 
         * @return This object (fluent setter)
         */
        public CreateCertFromCSR setCaInfo(X500Name subject, PrivateKey key) {
            caSubject = subject;
            caKey = key;
            return this;
        }
        
        /**
         * Setter for the serial number of the resulting certificate.
         * 
         * @param serial
         *            The serial number of the resulting certificate.
         * 
         * @return This object (fluent setter)
         */
        public CreateCertFromCSR setSerial(int serial) {
            certSerial = serial;
            return this;
        }

        /**
         * Setter for the effective period of the certificate.
         * Defaults to a period starting now and ending in approximately 3
         * years.
         * 
         * @param begin
         *            The earliest effective date.
         * @param end
         *            The latest effective date.
         * 
         * @return This object (fluent setter)
         */
        public CreateCertFromCSR setPeriod(Date begin, Date end) {
            beginDate = begin;
            endDate = end;
            return this;
        }

        /**
         * Creates the certificate.
         * 
         * @return The resulting certificate.
         */
        public X509Certificate create() {
            InputStream stream = null;
            try {
                SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(requesterPubKey.getEncoded());
                
                X509v3CertificateBuilder myCertificateGenerator = new X509v3CertificateBuilder(
                        caSubject,
                        BigInteger.valueOf(certSerial),
                        beginDate,
                        endDate,
                        request.getSubject(),
                        keyInfo);
                
                AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA512withRSA");
                AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
                AsymmetricKeyParameter keyParam = PrivateKeyFactory.createKey(caKey.getEncoded());
                ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(keyParam);        

                X509CertificateHolder holder = myCertificateGenerator.build(sigGen);
                Certificate cert = holder.toASN1Structure();
                
                CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
                
                stream = new ByteArrayInputStream(cert.getEncoded());
                X509Certificate theCert = (X509Certificate) cf.generateCertificate(stream);
                return theCert;
            } catch (RuntimeException | GeneralSecurityException | IOException | OperatorCreationException e) {
                throw new RuntimeException("Failed to write certificate to sign CSR into certificate", e);
            } finally {
                CloseablesExt.closeQuietly(stream);
            }
        }
    }

    private static enum PemType {
        PRIVATE_KEY("PRIVATE KEY"),
        PUBLIC_KEY("PUBLIC KEY"),
        CERTIFICATE_REQUEST("CERTIFICATE REQUEST");
        
        private String value;
        
        PemType(String value) {
            this.value = value;
        }
    }
}
