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

import org.apache.commons.io.IOUtils;
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

public class CertUtils {

    // Ensure the "BC" provider is defined
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    /**
     * Static accessor.
     */
    public static final CertUtils I = new CertUtils();
    
    private static final long YEARS_3_MS = 1000L * 60 * 60 * 24 * 364 * 3;
    
    /**
     * Generates a new, random public/private key pair.
     */
    public KeyPair createKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096);
            return keyPairGenerator.generateKeyPair();
        } catch (RuntimeException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create public/private key pair", e);
        }
    }

    /**
	 * Creates a new CA (self-signed) certificate.
	 * 
	 * @param keyPair
	 *            The existing public/private key pair for the certificate.
	 * @param subject
	 *            The subject of the new certificate.
	 * @param beginDate
	 *            The earliest timestamp at which the certificate is effective.
	 * @param endDate
	 *            The timestamp at which the certificate expires.
	 * @param certSerial
	 *            The serial number to apply to the certificate. Note that all
	 *            certificates from a single certificate authority must be
	 *            guaranteed to have unique serial numbers, so this value needs
	 *            to be managed externally. Since this is a self-signed CA, a
	 *            value of 1 is generally acceptable here.
	 */
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
            
            SubjectPublicKeyInfo subjPubKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
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
    
    /**
	 * Creates a new Certificate Signing Request (CSR).
	 * 
	 * @param keyPair
	 *            The existing public/private key pair owned by the requestor.
	 * @param subject
	 *            The subject of the requestor.
	 */
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
    
    /**
	 * Used to sign a CSR, which will result in a new certificate. This method
	 * actually returns an instance of {@link CreateCertFromCSR}, which is used
	 * to collect arguments before executing the certificate creation.
	 */
    public CreateCertFromCSR createCertFromCSR() {
        return new CreateCertFromCSR(); 
    }

    /**
	 * Reads a private key from a PEM-encoded file.
	 */
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

    /**
	 * Reads a public key from a PEM-encoded file.
	 */
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

    /**
	 * Reads an CSR from a PEM-encoded file.
	 */
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

    /**
	 * Reads an X509 certificate from a PEM-encoded file.
	 */
    public X509Certificate readCertificateFromPem(File file) {
        FileReader reader = null;
        PEMParser parser = null;
        try {
            reader = new FileReader(file);
            parser = new PEMParser(reader);
            X509CertificateHolder holder = (X509CertificateHolder) parser.readObject();
            return new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
        } catch (RuntimeException | IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to load certficate from ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(parser);
            CloseablesExt.closeQuietly(reader);
        }
    }

    /**
	 * Reads a Java keystore (JKS) from a file.
	 */
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

    /**
	 * Writes a private key to disk, in DER format.
	 */
    public void writeToDerFile(PrivateKey key, File file) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            IOUtils.write(key.getEncoded(), stream);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("Failed to write key DER to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(stream);
        }
    }

    /**
	 * Writes an X509 certificate to disk, in DER format.
	 */
    public void writeToDerFile(X509Certificate cert, File file) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            IOUtils.write(cert.getEncoded(), stream);
        } catch (RuntimeException | IOException | CertificateEncodingException e) {
            throw new RuntimeException("Failed to write certificate DER to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(stream);
        }
    }

    /**
	 * Writes a private key to to disk, in PEM format.
	 */
    public void writeToPemFile(PrivateKey key, File file) {
        writeToPemFile(PemType.PRIVATE_KEY, key.getEncoded(), file);
    }

    /**
	 * Writes a public key to to disk, in PEM format.
	 */
    public void writeToPemFile(PublicKey key, File file) {
        writeToPemFile(PemType.PUBLIC_KEY, key.getEncoded(), file);
    }

    /**
	 * Writes an X509 certificate to disk, in PEM format.
	 */
    public void writeToPemFile(X509Certificate cert, File file) {
        try {
	        writeToPemFile(PemType.CERTIFICATE, cert.getEncoded(), file);
	    } catch (CertificateEncodingException e) {
	        throw new RuntimeException("Failed to write certificatet PEM to ["+file+"]", e);
	    }
    }

    /**
	 * Writes a CSR to disk, in PEM format.
	 */
    public void writeToPemFile(PKCS10CertificationRequest request, File file) {
        try {
            writeToPemFile(PemType.CERTIFICATE_REQUEST, request.getEncoded(), file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSR PEM to ["+file+"]", e);
        }
    }

    /**
	 * Writes an arbitrary byte array to disk, in PEM format.
	 */
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

    /**
	 * Writes a {@link KeyStore} to disk.
	 */
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

    /**
	 * Writes a certificate, its full certificate chain, and its private key to
	 * a P12/PFX file.
	 * 
	 * @param certChain
	 *            The full certificate chain of the target certificate, in order
	 *            from the target certificate to the root CA.
	 * @param key
	 *            The private key of the target certificate.
	 * @param password
	 *            The password to assign to the new P12 file.
	 * @param file
	 *            The file to create.
	 */
    public void writeToP12(X509Certificate[] certChain, PrivateKey key, char[] password, File file) {
        OutputStream stream = null;
        try {
        	X509Certificate targetCert = certChain[0];
            stream = new FileOutputStream(file);
            KeyStore ks = KeyStore.getInstance("PKCS12", "BC");   
            ks.load(null,null);
            ks.setCertificateEntry(targetCert.getSerialNumber().toString(), targetCert);
            ks.setKeyEntry(targetCert.getSerialNumber().toString(), key, password, certChain);
            ks.store(stream, password);
        } catch (RuntimeException | IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to write PKCS12 to ["+file+"]", e);
        } finally {
            CloseablesExt.closeQuietly(stream);
        }
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
        CERTIFICATE("CERTIFICATE"),
        CERTIFICATE_REQUEST("CERTIFICATE REQUEST");
        
        private String value;
        
        PemType(String value) {
            this.value = value;
        }
    }
}
