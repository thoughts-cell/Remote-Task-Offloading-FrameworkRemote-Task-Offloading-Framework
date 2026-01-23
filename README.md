# Secured Remote Task Offloading Framework

A secure distributed computing framework that enables clients to offload computational tasks to remote servers over a network. The system employs advanced security mechanisms including asymmetric and symmetric encryption to protect data integrity and confidentiality during task execution and result transmission.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Usage Guide](#usage-guide)
- [Security Implementation](#security-implementation)
- [Supported Tasks](#supported-tasks)
- [Known Issues & Fixes](#known-issues--fixes)

## Overview

The Secured Remote Task Offloading Framework is a Java-based distributed system that allows clients to securely submit computational tasks to remote servers for processing. The framework handles authentication, data encryption, secure communication, and result delivery using a robust client-server architecture with multi-threaded request handling.

## Features

- **Secure Communication**: RSA asymmetric encryption for key exchange and AES symmetric encryption for data transmission
- **Client Authentication**: Multi-user support with individual RSA key pairs for each client
- **Task Offloading**: Clients can offload compute-intensive tasks to remote servers
- **Multi-threaded Server**: Handles multiple concurrent client connections
- **Object Serialization**: Efficient task and result transfer over network streams
- **Dynamic Class Loading**: Servers can receive and execute custom task implementations
- **Result Encryption**: Results are encrypted before transmission back to clients

## Architecture

### Client-Server Model

```
┌─────────────────┐          Network              ┌──────────────────┐
│  Compute Client │  ←─────────────────────────→  │  Compute Server  │
│  (GUI)          │    TCP Sockets & Encryption   │  (Multi-threaded)|
└─────────────────┘                               └──────────────────┘
```

### Security Flow

1. **Initialization**: Client and Server exchange public keys
2. **Authentication**: Client authenticates with server using digital signatures
3. **Session Key Exchange**: Asymmetric encryption used to securely share AES session key
4. **Task Submission**: Tasks encrypted with session key and transmitted
5. **Result Transmission**: Results encrypted and returned to client for decryption

## Project Structure

```
Secured-Remote-Task-Offloading-Framework/
├── KeyGenForGroup/                          # Key generation utility
│   └── src/groupkeygen/                     # Key pair generation for group members
│
├── SecuredRemoteOffloadTaskComputeClient/   # Client Application
│   ├── src/
│   │   ├── computeclient/
│   │   │   └── MainFrame.java               # GUI and client logic
│   │   ├── Contract/                        # Task interface and implementations
│   │   │   ├── Task.java                    # Base task interface
│   │   │   ├── Fibonacci.java               # Fibonacci computation task
│   │   │   ├── PerfectNumber.java           # Perfect number search task
│   │   │   ├── Factorization.java           # Number factorization task
│   │   │   ├── CSAuthenticator.java         # Authentication contract
│   │   │   └── CFile.java                   # File transfer contract
│   │   └── Security/
│   │       └── SecurityUtil.java            # Encryption/decryption utilities
│   └── [Key files: Michael Fox, Stephen Smith, Centre keys]
│
├── SecuredRemoteOffloadTaskComputeServer/   # Server Application
│   ├── src/
│   │   ├── computeserver/
│   │   │   ├── ComputeServer.java           # Server entry point
│   │   │   └── Connection.java              # Handles individual client connections
│   │   ├── Contract/                        # Task definitions
│   │   ├── Security/
│   │   │   └── SecurityUtil.java            # Security utilities
│   │   └── test/                            # Test files
│   └── [Key files: Same as client]
│
└── README.md                                 # This file
```

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Ant (for building with build.xml)
- NetBeans IDE (optional, project structure suggests NetBeans project)

### System Requirements

- Operating System: Windows, Linux, or macOS
- Memory: 512 MB minimum
- Network: TCP connectivity on port 6789

## Building the Project

### Using Ant

Navigate to each module directory and build:

```bash
# Build the Key Generation utility
cd KeyGenForGroup
ant build

# Build the Compute Server
cd ../SecuredRemoteOffloadTaskComputeServer
ant build

# Build the Compute Client
cd ../SecuredRemoteOffloadTaskComputeClient
ant build
```

### Using NetBeans

1. Open the project in NetBeans
2. Right-click on the project
3. Select "Build Project" or "Clean and Build Project"

## Running the Application

### Step 1: Start the Compute Server

```bash
cd SecuredRemoteOffloadTaskComputeServer
java -cp build/classes computeserver.ComputeServer
```

Expected output:
```
-------------------------------------
The server is listening on port 6789 for object transfer...
-----------------------------------
```

### Step 2: Run the Compute Client

```bash
cd SecuredRemoteOffloadTaskComputeClient
java -cp build/classes computeclient.MainFrame
```

The client GUI will launch with options to:
- Connect to the server
- Authenticate
- Upload task class files
- Submit tasks for execution
- View results

## Usage Guide

### Authentication

1. Launch the client application
2. Click "Auth" button to authenticate with the server
3. The client sends authentication request to the server using its private key
4. Server validates the signature and establishes a secure session

### Uploading Task Classes

1. Click "Upload" to send task class files to the server
2. The framework supports uploading:
   - `Fibonacci.class` - Fibonacci sequence computation
   - `PerfectNumber.class` - Perfect number search
   - `Factorization.class` - Integer factorization

### Submitting Tasks

1. Select a task from the dropdown menu
2. Enter task parameters (if required)
3. Click "Offload Task"
4. The server processes the task and returns encrypted results
5. Results are automatically decrypted and displayed

### Example Tasks

#### Fibonacci Sequence
- **Parameters**: Sequence length (e.g., 10)
- **Output**: First N Fibonacci numbers

#### Perfect Number Search
- **Parameters**: Upper limit (e.g., 10000)
- **Output**: All perfect numbers up to the limit

#### Number Factorization
- **Parameters**: Number to factorize (e.g., 12345)
- **Output**: Prime factors of the number

## Security Implementation

### Key Management

- **Centre Key Pair**: Used for group key management
- **Individual Key Pairs**: Each client (Michael Fox, Stephen Smith) has unique RSA keypair
- **Server Keys**: Server holds public keys of all clients

### Encryption Algorithms

- **RSA**: 2048-bit keys for asymmetric encryption
- **AES**: 256-bit keys for symmetric encryption
- **SHA-256**: For cryptographic hashing

### Data Protection

1. **In Transit**: All task data and results encrypted with session AES key
2. **Authentication**: Digital signatures verify client identity
3. **Integrity**: Symmetric encryption ensures data hasn't been tampered with

### Key Files

- `*-pub.ser`: Public key serialized objects
- `*-pri.ser`: Private key serialized objects (stored securely)
- `CentrePub.ser` / `CentrePri.ser`: Centre authority keys

## Supported Tasks

The framework includes three sample task implementations:

### 1. Fibonacci Computation
Generates Fibonacci sequences up to a specified length.

### 2. Perfect Number Search
Identifies perfect numbers within a given range. Perfect numbers equal the sum of their proper divisors (e.g., 6 = 1 + 2 + 3).

### 3. Factorization
Computes prime factorization of a given integer.

Tasks must implement the `Task` interface and can be dynamically loaded on the server.


## Development Notes

### Code Organization

- **Contract Classes**: Define interfaces and serializable data structures
- **Security Utilities**: Centralized cryptographic operations
- **Connection Handler**: Each server thread manages one client connection
- **GUI Components**: Client-side user interface using Swing


   
![authentication](https://github.com/user-attachments/assets/25bc4c7d-fbd5-4ccf-98f8-15d269de661e)

![Screenshot 2026-01-22 165956](https://github.com/user-attachments/assets/829748d2-4782-4ddb-81d3-51c3f7176eb5)

![result](https://github.com/user-attachments/assets/5623f235-54a1-4a4b-8a9a-bea67e8e9314)

