# Diffie-Hellman-Key-Exchange

This project implements the Diffie-Hellman Key Exchange algorithm in Java. It securely exchanges keys between two parties (Alice and Bob) to enable encrypted communication using symmetric cryptography.
---

## Project Overview

The Diffie-Hellman Key Exchange algorithm establishes a shared secret key between two parties over a public network. This project uses Java to implement the algorithm and provides encryption and decryption features using the derived key.

### Key Features
- **Key Exchange:**: Securely exchanges keys between Alice and Bob using large prime numbers and modular arithmetic.
- **Symmetric Encryption**: Encrypts and decrypts messages using the shared key.
- **Client-Server Communication:**: Simulates a messaging system where multiple clients communicate through a server.
- **Secure Messaging**: Messages are encrypted before being sent, ensuring privacy over the network.

---

## How It Works

### Key Generation
- Alice and Bob each generate a private key.
- They compute their public keys using a shared prime (p) and base (g):
PublicKey = (g ^ PrivateKey) mod p
- Public keys are exchanged between the parties.

### Shared Key Derivation
- Each party computes the shared secret key using the received public key:
 SharedKey = (OtherPublicKey ^ PrivateKey) mod p

### Message
- The shared secret key is used to encrypt and decrypt messages.
- Communication is facilitated via a server that routes encrypted messages between clients.

---

## Project Structure

src/
├── presentation/              # GUI and client-server interfaces
│   ├── ServerApp.java         # Server-side application
│   ├── ClientApp.java         # Client-side application
├── businesslayer/             # Business logic and encryption mechanisms
│   ├── abstracts/             # Interfaces
│   │   ├── IClientHandler.java
│   │   ├── IOscar.java
│   │   ├── IServerHandler.java
│   └── concrete/              # Implementations
│       ├── ClientHandler.java
│       ├── DataEncryptionStandard.java
│       ├── Oscar.java
│       ├── ServerHandler.java
├── datalayer/                 # Data access and management
│   ├── abstracts/             # Interfaces
│   │   ├── IClientManager.java
│   │   ├── IServerManager.java
│   └── concrete/              # Implementations
│       ├── ClientManager.java
│       ├── DiffieHellmanKey.java
│       ├── GenerateResult.java
│       ├── ServerManager.java
│       ├── ClientData.java
└── .gitignore                 # Git ignore rules


---

## Installation and Usage

### Prerequisites
- **Java JDK**:Version 8 or above
- **IDE**: IntelliJ IDEA, Eclipse, or any Java-compatible IDE

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Oguzdoc/Diffie-Hellman-Key-Exchange.git
   cd diffie-hellman-key-exchange
   
2. Open the project in your preferred IDE.
3. Build and run the project.

## Running the Application

### Server
1. Open the presentation.ServerApp class in your IDE.

2. Run the class to start the server.

### Client
1. Open the presentation.ClientApp class in your IDE.

2. Run the class to start a client instance.

3. Start additional clients by running ClientApp in separate instances.

