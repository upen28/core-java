package com.payment.cryptography;

public class SSLInfo {

    /*
    
    SSL handshake protocol --- SSL cipher change protocol --- SSL alert protocol --Application protocol HTTP LDAP
    
    
    SSL cipher change protocol
    
    This protocol is the simplest SSL protocol. It consists of a single message that carries the value of 1. 
    The sole purpose of this message is to cause the pending session state to be established as a fixed state, which results,
    for example, in defining the used set of protocols. This type of message must be sent by the client to the server and vice versa. 
    After exchange of messages, the session state is considered agreed. This message and any other SSL messages are transferred using the SSL record protocol
    
    
    
    The Alert Protocol
    
    The Alert Protocol is used by parties to convey session messages associated with data exchange and functioning of the protocol. 
    Each message in the alert protocol consists of two bytes. The first byte always takes a value, "warning" (1) or "fatal" (2) ,
    that determines the severity of the message sent. Sending a message having  a "fatal" status by either party will result in an immediate termination of the SSL session. 
    The next byte of the message contains one of the defined error codes, which may occur during an SSL communication session. 
    
    
    
    SSL Record protocol
    
    1 : Application Data
    2 : fragmentation of application data
    3 : compression of data
    4 : data integrity of data (MAC)
    5 : encryption of data
    6 : creating a header [ Content type(1 byte) define the higher level protocol . Major Version(1 byte) SSL major version . Minor Version(1 byte) SSL minor version. 
     						 Compressed length(2 byte) length of data ]
    
    TCP
    
    IP
     
     */

    public static void main(String... args) {

    }

}
