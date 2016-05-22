#!/bin/bash

# ----------------
# This file generates sample PKI data for
# use in testing our analagous Java utilties.
# You'd need to run this in linux or cygwin
# to generate the sample files again.
# (...or translate this bash file to batch)
#
# Note that everytime you re-run this script,
# you'll have to identify the new timestamp.
# This script echos those dates out at the end.
# ----------------

# Fail on any error
set -e

LEN=4096
DAYS=365

# Remove old files
rm -f *.{csr,key,crt,pub}

cert_serial=0

# Generate CA (self-signed) cert and private key
cert_serial=$((cert_serial+1))
openssl req -new -x509 -nodes -out ca.pem.crt -keyout ca.pem.key \
    -days $DAYS -subj "/CN=ca" -newkey rsa:$LEN -sha512

# Generate a CSR and private key for a child cert
openssl req -new -out child.csr -keyout child.pem.key -nodes \
    -newkey rsa:$LEN -subj "/CN=child"

# Sign the child CSR (generate the child cert)
# > NOTE: "-extfile openssl.cnf" is required to force the certificate
# > to X509 v3, which matches Java's implementation.
cert_serial=$((cert_serial+1))
openssl x509 -req -in child.csr -CAkey ca.pem.key -CA ca.pem.crt \
    -days $DAYS -set_serial $cert_serial -sha512 -out child.pem.crt \
	-extfile openssl.cnf

# Export the child key to DER format
openssl pkcs8 -topk8 -inform PEM -outform DER \
    -in child.pem.key -out child.der.key -nocrypt

# Export the certs in DER format
openssl x509 -outform der -in ca.pem.crt -out ca.der.crt
openssl x509 -outform der -in child.pem.crt -out child.der.crt

# Export the pub portions of the keys
openssl rsa -in ca.pem.key -pubout > ca.pem.key.pub
openssl rsa -in child.pem.key -pubout > child.pem.key.pub

echo ""
echo "==== New Certificate Dates (Update Java Code!) ===="
echo "ca.pem.crt:"
openssl x509 -in ca.pem.crt -text -noout | grep "Not Before"
openssl x509 -in ca.pem.crt -text -noout | grep "Not After"
echo "child.pem.crt:"
openssl x509 -in child.pem.crt -text -noout | grep "Not Before"
openssl x509 -in child.pem.crt -text -noout | grep "Not After"
echo "========"
