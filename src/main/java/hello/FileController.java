package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static String fileName;
    private static String binName;
    private static String signature;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        fileName = fileStorageService.storeFile(file);

        return new UploadFileResponse(fileName,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<UploadFileResponse> list = Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());

        if (list.size() == 2) {
            binName = list.get(0).getFileName();
            signature = list.get(1).getFileName();
        } else  {
            for (int i = 0; i < list.size(); i++)   {
                File file = new File("uploads/bin/" + list.get(i).getFileName());
                file.delete();
            }
        }
        return list;
    }

    @PostMapping("/verifyJar")
    public String   verifyJar() throws Exception{

        FileInputStream certfis = new FileInputStream("signedCertLast");
        java.security.cert.CertificateFactory cf =
                java.security.cert.CertificateFactory.getInstance("X.509");
        java.security.cert.Certificate cert =  cf.generateCertificate(certfis);

        Thread.sleep(1000);
        if (!(fileName.endsWith(".jar")))  {
            File file = new File("uploads/bin/" + fileName);
            file.delete();
            return "Please introduce .jar file";
        }
        deleteJarFiles();
        return(verify(new JarFile("uploads/jars/" + fileName), (X509Certificate)cert));
    }

    @PostMapping("/verifyBin")
    public String   verifyBin() throws Exception{

        Thread.sleep(200);
        String certName = "";
        String dataFile = "uploads/bin/" + binName;
        String signFile = "uploads/bin/" + signature;

        Path path = Paths.get(signFile);
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (Exception e)   {
            return "Please submit only one time";
        }

        byte[] slice;
        try {
            slice = new byte[1];
        } catch (Exception e)   {
            return "Please submit only one time";
        }

        try {
            System.arraycopy(bytes, bytes.length - 1, slice, 0, 1);
        } catch (Exception e)   {
            return "Please submit only one time";
        }

        int number = (int)slice[0];

        if (number > 9 || number < 1) {
            deleteBinFiles(dataFile,signFile);
            return "Not OK";
        }

        File path2 = new File("Cert/cert/");
        File[] files = path2.listFiles();

        try {
            certName = files[number - 1].getPath();
        } catch (Exception e)   {
            deleteBinFiles(dataFile,signFile);
            return "Please submit only one time";
        }

        FileInputStream certfis = new FileInputStream(certName);
        java.security.cert.CertificateFactory cf =
                java.security.cert.CertificateFactory.getInstance("X.509");
        java.security.cert.Certificate cert =  cf.generateCertificate(certfis);

        PublicKey pub = cert.getPublicKey();

        Signature sign = Signature.getInstance("SHA256withRSA");

        sign.initVerify(pub);

        InputStream in = null;

        try {
            try {
                in = new FileInputStream(dataFile);
            } catch (Exception e)   {
                return "Please submit only one time";
            }
            byte[] buf = new byte[2048];
            int len;
            while ((len = in.read(buf)) != -1) {
                sign.update(buf, 0, len);
            }
        } finally {
            if ( in != null ) in.close();
        }

	/* Read the signature bytes */
	    Path path3;
	    try {
            path3 = Paths.get(signFile);
        }catch (Exception e)    {
	        return "Please submit only one time";
        }

        byte[] bytes2;

	    try {
            bytes2 = Files.readAllBytes(path3);
        } catch (Exception e)   {
	        return "Please submit only one time";
        }

        byte[] bytes3;
        try {
            bytes3 = new byte[bytes2.length - 1];
        } catch (Exception e)   {
            return "Please submit only one time";
        }

        System.arraycopy(bytes2, 0, bytes3, 0, bytes2.length - 1);

        boolean response = sign.verify(bytes3);
        if (response == true)   {
            deleteBinFiles(dataFile,signFile);
            return "OK";
        } else {
            deleteBinFiles(dataFile,signFile);
            return "Not OK";
        }
    }


    @PostMapping("/verifyBin2")
    public String   verifyBin2() throws Exception{

        Thread.sleep(200);
        String certName = "";
        String dataFile = "uploads/bin/" + signature;
        String signFile = "uploads/bin/" + binName;

        Path path = Paths.get(signFile);
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (Exception e)   {
            return "Please submit only one time";
        }

        byte[] slice;
        try {
            slice = new byte[1];
        } catch (Exception e)   {
            return "Please submit only one time";
        }

        try {
            System.arraycopy(bytes, bytes.length - 1, slice, 0, 1);
        } catch (Exception e)   {
            return "Please submit only one time";
        }

        int number = (int)slice[0];

        if (number > 9 || number < 1) {
            deleteBinFiles(dataFile,signFile);
            return "Not OK";
        }

        File path2 = new File("Cert/cert/");
        File[] files = path2.listFiles();

        try {
            certName = files[number - 1].getPath();
        } catch (Exception e)   {
            deleteBinFiles(dataFile,signFile);
            return "Please submit only one time";
        }

        FileInputStream certfis = new FileInputStream(certName);
        java.security.cert.CertificateFactory cf =
                java.security.cert.CertificateFactory.getInstance("X.509");
        java.security.cert.Certificate cert =  cf.generateCertificate(certfis);

        PublicKey pub = cert.getPublicKey();

        Signature sign = Signature.getInstance("SHA256withRSA");

        sign.initVerify(pub);

        InputStream in = null;

        try {
            try {
                in = new FileInputStream(dataFile);
            } catch (Exception e)   {
                return "Please submit only one time";
            }
            byte[] buf = new byte[2048];
            int len;
            while ((len = in.read(buf)) != -1) {
                sign.update(buf, 0, len);
            }
        } finally {
            if ( in != null ) in.close();
        }

	/* Read the signature bytes */
        Path path3;
        try {
            path3 = Paths.get(signFile);
        }catch (Exception e)    {
            return "Please submit only one time";
        }

        byte[] bytes2;

        try {
            bytes2 = Files.readAllBytes(path3);
        } catch (Exception e)   {
            return "Please submit only one time";
        }

        byte[] bytes3;
        try {
            bytes3 = new byte[bytes2.length - 1];
        } catch (Exception e)   {
            return "Please submit only one time";
        }

        System.arraycopy(bytes2, 0, bytes3, 0, bytes2.length - 1);

        boolean response = sign.verify(bytes3);
        if (response == true)   {
            deleteBinFiles(dataFile,signFile);
            return "OK";
        } else {
            deleteBinFiles(dataFile,signFile);
            return "Not OK";
        }
    }

    private void deleteBinFiles(String dataFile, String signFile) throws InterruptedException {

        Thread n1 = new DeleteFilesThread(dataFile, signFile, 2);
        n1.start();
    }


    private static void deleteJarFiles() throws  InterruptedException   {
        Thread n2 = new DeleteFilesThread("uploads/jars/" + fileName, "uploads/jars/" + fileName, 1);
        n2.start();
    }



    private static String verify(JarFile jar, X509Certificate targetCert) throws Exception{

        if (targetCert == null) {
            return ("Provider certificate is invalid");
        }

        try {
            if (jar == null) {
                return ("Jar file wasn't specified.");
            }
        } catch (Exception ex) {
            SecurityException se = new SecurityException();
            se.initCause(ex);
            return (se.toString());
        }

        Vector<JarEntry> entriesVec = new Vector<JarEntry>();
        byte[] buffer = new byte[8192];
        Enumeration entries = jar.entries();

        // Ensure the jar file is signed.
        Manifest man = jar.getManifest();
        if (man == null) {
            return "The provider is not signed";
        }

        while (entries.hasMoreElements()) {
            JarEntry je = (JarEntry) entries.nextElement();

            try {
                // Skip directories.
                if (je.isDirectory()) {
                    continue;
                }

                entriesVec.addElement(je);
                InputStream is = jar.getInputStream(je);

                // Read in each jar entry. A security exception will
                // be thrown if a signature/digest check fails.
                int n;
                while ((n = is.read(buffer, 0, buffer.length)) != -1) {
                    // Don't care
                }

                is.close();
            } catch (SecurityException se) {
                return "Something doesn't work properly.";
            }
        }

        Enumeration e = entriesVec.elements();

        while (e.hasMoreElements()) {


            JarEntry je = (JarEntry) e.nextElement();

            // Every file must be signed except files in META-INF.
            Certificate[] certs = je.getCertificates();
            if ((certs == null) || (certs.length == 0)) {
                if (!je.getName().startsWith("META-INF")) {
                    return ("The provider " +
                            "has unsigned " +
                            "class files.");
                }
            } else {
                // Check whether the file is signed by the expected
                // signer. The jar may be signed by multiple signers.
                // See if one of the signers is 'targetCert'.
                int startIndex = 0;
                X509Certificate[] certChain;
                boolean signedAsExpected = false;

                while ((certChain = getAChain(certs, startIndex)) != null) {
                    if (certChain[0].equals(targetCert)) {
                        // Stop since one trusted signer is found.
                        signedAsExpected = true;
                        break;
                    }
                    // Proceed to the next chain.
                    startIndex += certChain.length;
                }

                if (!signedAsExpected) {
                    return("The provider " +
                            "is not signed by a " +
                            "trusted signer");
                }
            }
        }
        return "merge";
    }

    private static X509Certificate[] getAChain(Certificate[] certs,
                                               int startIndex) {
        if (startIndex > certs.length - 1)
            return null;

        int i;
        // Keep going until the next certificate is not the
        // issuer of this certificate.
        for (i = startIndex; i < certs.length - 1; i++) {
            if (!((X509Certificate)certs[i + 1]).getSubjectDN().
                    equals(((X509Certificate)certs[i]).getIssuerDN())) {
                break;
            }
        }
        // Construct and return the found certificate chain.
        int certChainSize = (i-startIndex) + 1;
        X509Certificate[] ret = new X509Certificate[certChainSize];
        for (int j = 0; j < certChainSize; j++ ) {
            ret[j] = (X509Certificate) certs[startIndex + j];
        }
        return ret;
    }
}