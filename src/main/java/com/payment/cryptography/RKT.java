package com.payment.cryptography;

public class RKT {

    /*
     
     RFC 5652
     A Request for Comments (RFC) is a formal document from the Internet Engineering Task Force ( IETF ) 
     
     
     Host Certification Request PKCS#10
     EPP-ED certificate PKCS#7
     EPP-SV certificate PKCS#7
     Host certificate   PKCS#7 
     Master Key Block   PKCS#7
     Master Key Confirmation PKCS#7
      
     ASN.1 Abstract Syntax Notation One
     
       ContentInfo ::= SEQUENCE {
    contentType ContentType,
    content [0] EXPLICIT ANY DEFINED BY contentType }
    
       SignedData ::= SEQUENCE {
    version CMSVersion,
    digestAlgorithms DigestAlgorithmIdentifiers,
    encapContentInfo EncapsulatedContentInfo,
    certificates [0] IMPLICIT CertificateSet OPTIONAL,
    crls [1] IMPLICIT RevocationInfoChoices OPTIONAL,
    signerInfos SignerInfos }
    
    EncapsulatedContentInfo ::= SEQUENCE {
    eContentType ContentType,
    eContent [0] EXPLICIT OCTET STRING OPTIONAL } 
    
    EnvelopedData ::= SEQUENCE {
    version CMSVersion,
    originatorInfo [0] IMPLICIT OriginatorInfo OPTIONAL,
    recipientInfos RecipientInfos,
    encryptedContentInfo EncryptedContentInfo,
    unprotectedAttrs [1] IMPLICIT UnprotectedAttributes OPTIONAL }
    
    KeyTransRecipientInfo ::= SEQUENCE {
    version CMSVersion,  -- always set to 0 or 2
    rid RecipientIdentifier,
    keyEncryptionAlgorithm KeyEncryptionAlgorithmIdentifier,
    encryptedKey EncryptedKey }
           
      
    SignerInfo ::= SEQUENCE {
    version CMSVersion,
    sid SignerIdentifier,
    digestAlgorithm DigestAlgorithmIdentifier,
    signedAttrs [0] IMPLICIT SignedAttributes OPTIONAL,
    signatureAlgorithm SignatureAlgorithmIdentifier,
    signature SignatureValue,
    unsignedAttrs [1] IMPLICIT UnsignedAttributes OPTIONAL }   
    
     
     RKT flow 
        
         Rb
         RcAndRD
         Rd
         Re
         Rg
     	  
     */

    public static void main(String... args) {

    }

}
