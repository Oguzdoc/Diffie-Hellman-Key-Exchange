# Diffie-Hellman-Key-Exchange
Implementation of the Diffie-Hellman Key Exchange algorithm in Python for secure key exchange, with encryption and decryption using symmetric cryptography.


# Diffie-Hellman Key Exchange

This project implements the Diffie-Hellman Key Exchange algorithm using Python. The purpose of this project is to establish a shared secret key between two parties, Alice and Bob, to enable secure communication over a public network. The implementation includes creating public and private keys, using large prime numbers as the base, and encrypting/decrypting messages with symmetric encryption based on the shared key.

## Project Overview

In this project, Alice and Bob will exchange keys using the Diffie-Hellman algorithm, leveraging the elliptic curve and large prime numbers. They will each generate a private key and derive a public key, which they exchange. By calculating the shared secret key, they can then encrypt and decrypt messages securely. We also aim to test and analyze the security of this system, including attempting attacks on the encryption to understand potential vulnerabilities.
