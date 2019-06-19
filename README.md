# caver-java: Caver Java Klaytn Dapp API

caver-java is a lightweight, high modular, convenient Java and Android library to interact with clients (nodes) on the Klaytn network:

This library is an interface which allow you to communicate with [Klaytn](https://www.klaytn.com) network between service and platform.

## Features
- Complete implementation of Klaytn’s JSON-RPC client API over HTTP and IPC
- Various transaction, account, account key types support
- Auto-generation of Java smart contract wrappers to create, deploy, transact with and call smart contracts from native Java code
- Klaytn wallet support
- Command line tools
- Android compatible
## Getting started
#### maven

```groovy
<dependency>
  <groupId>com.klaytn.caver</groupId>
  <artifactId>core</artifactId>
  <version>0.9.4</version>
  <type>pom</type>
</dependency>
```
#### gradle
```groovy
compile 'com.klaytn.caver:core:0.9.4'
```
If you want to use Android dependency, just append -android at the end of version. (e.g. 0.9.4-android)
## Start a client
If you want to run your own EN(Endpoint Node), click [here](https://docs.klaytn.com/node/en) to set up.
Or use Klaytn Public EN
```java
Caver caver  = Caver.build("https://api.cypress.klaytn.net:8651/");
```
## Transactions
When you send transactions, caver-java provides easy-to-use models. Here's an example of transferring value using your Klaytn wallet file:
```java
Caver caver = Caver.build(<endpoint>);
KlayCredentials credentials = KlayWalletUtils.loadCredentials(<password>, <walletfilePath>);
KlayTransactionReceipt.TransactionReceipt transactionReceipt = ValueTransfer.sendFunds(
            caver, credentials, <address>, 
            <value>, <valueUnit>, <gasLimit>)
            .send();
```
#### Fee Delegation
Klaytn provides [Fee Delegation](https://docs.klaytn.com/klaytn/design/transactions#fee-delegation) feature. Here's example code in perspective of sender or fee payer.
*When you are sender:*
```java
KlayCredentials sender = KlayWalletUtils.loadCredentials(<password>, <walletfilePath>);
TxTypeFeeDelegatedValueTransferMemo tx = TxTypeFeeDelegatedValueTransferMemo.createTransaction(
            <nonce>, <gasPrice>, <gasLimit>, <toAddress>, 
            <value>, sender.getAddress(), <memo>
);
String transactionHash = tx.sign(sender, <chainID>).getValueAsString();
```
After signing a transaction, sender can get 32 bytes transaction hash. Just send the transaction hash to fee payer who will pay for transaction fee instead.
*When you are fee payer:*
```java
Caver caver = Caver.build(<endpoint>);
KlayCredentials feePayer = KlayWalletUtils.loadCredentials(<password>, <walletfilePath>);
FeePayerManager feePayerManager = new FeePayerManager.Builder(caver, feePayer).build();
feePayerManager.executeTransaction(transactionHash);
```
After fee payer get transaction hash from sender, fee payer can easily send transaction using FeePayerManager model. We will cover Manger models(TransactionManager, FeePayerManger) in more detail from below.
For more information about Klaytn multiple transaction types, visit [klaytn docs](https://docs.klaytn.com/klaytn/design/transactions)
### Manager
There are manager models(TransactionManager, FeePayerManager) which help creating transaction object easily. In these managers' builder, you can customize some fields below.
- TransactionReceiptProcessor
- ErrorHandler
- GetNonceProcessor
## Klaytn Account
An account in Klaytn is a data structure containing information about a person's balance or a smart contract. If you require further information about Klaytn Account, you can refer to the [klaytn docs](https://docs.klaytn.com/klaytn/design/transactions).
### Account Key
An account key represents the key structure associated with an account.  Each account key has its own unique role. You can get more details of Account Key in [klaytn docs](https://docs.klaytn.com/klaytn/design/account#account-key). These are 6 types of Account Key in Klaytn:
- AccountKeyNil
- AccountKeyLegacy
- AccountKeyPublic
- AccountKeyFail
- AccountKeyWeightedMultiSig
- AccountKeyRoleBased

If you want to update the key of the given account:

```java
Caver caver = Caver.build(<endpoint>);
AccountUpdateTransaction accountUpdateTransaction = AccountUpdateTransaction.create(
      <fromAddress>,
      <newAccountKey>,
      <gasLimit>
);
Account.sendUpdateTransaction(caver, <from>, accountUpdateTransaction).send();
```

## contracts with Java smart contract wrappers
Caver provides auto-generate smart contract wrapper code to deploy contract or execute smart contract.
To generate the wrapper code, you need to compile smart contract.
```shell
$ solc <contract>.sol --bin --abi --optimize -o <output-dir>/
```
Then generate the wrapper code using caver-java’s Command line tools.
```shell
$ caver-java solidity generate -b <smart-contract>.bin -a <smart-contract>.abi -o <outputPath> -p <packagePath>
```
then you can get <smart-contract>.java which has various features as an output.
After generating the wrapper code, you can deploy your smart contract:

```java
Caver caver = Caver.build(<endpoint>);
KlayCredentials credentials = KlayWalletUtils.loadCredentials(<password>, <walletfilePath>);
<yourSmartContract> contract = <yourSmartContract>.deploy(
      caver, credentials, <chainId>, <contractGasProvider>,
      <param1>, ..., <paramN>).send();
```
After smart contract is deployed, you can load smart contract:
```java
Caver caver = Caver.build(<endpoint>);
KlayCredentials credentials = KlayWalletUtils.loadCredentials(<password>, <walletfilePath>);
<smartContract> contract = <smartContract>.load(
    <deployedContractAddress>, caver, credentials, <chainId>, <contractGasProvider>
);
```
To transact with a smart contract
```java
KlayTransactionReceipt.TransactionReceipt transactionReceipt = contract.<someMethod>(
      <param1>,
      ...).send();
```
To call a smart contract
```java
<type> result = contract.<someMethod>(<param1>, ...).send();
```
## Filters
TBD
## Web3j Similarity
We made caver-java as similar as possible to web3j for easy usability.
```java
/* start a client */
Web3j web3 = Web3j.build(new HttpService(<endpoint>)); // Web3j
Caver caver = Caver.build(new HttpService(<endpoint>)); // caver-java
/* get nonce */
BigInteger nonce = web3j.ethGetTransactionCount(<address>, <blockParam>).send().getTransactionCount(); // Web3j
Quantity nonce = caver.klay().getTransactionCount(<address>, <blockParam>).send().getValue(); // caver-java
/* convert unit */
Convert.toWei("1.0", Convert.Unit.ETHER).toBigInteger(); // Web3j
Convert.toPeb("1.0", Convert.Unit.KLAY).toBigInteger(); // caver-java
 
/* generate wallet file */
WalletUtils.generateNewWalletFile(<password>, <filepath>); // Web3j
KlayWalletUtils.generateNewWalletFile(<address>, <password>, <filepath>); // caver-java
/* load credentials */
Credentials credentials = WalletUtils.loadCrendetials(<password>, <filepath>"); // Web3j
KlayCredentials credentials = KlayWalletUtils.loadCredentials(<password>, <filepath>); // caver-java
/* Value Transfer */
TransactionReceipt transactionReceipt = Transfer.sendFunds(...),send(); // Web3j
KlayTransactionReceipt.TransactionReceipt transactionReceipt = ValueTransfer.sendFunds().send(); // caver-java
```
## Command line tools
A caver-java fat jar is distributed with open repository. The 'caver-java' allow you to use some of the functionality of caver-java from the command line: 
- Generate Solidity smart contract function wrappers
Installation
```shell
brew install caver-java
```
After installation you can run command 'caver-java'
```shell
$ caver-java
 ________  ________  ___      ___ _______   ________                              
|\   ____\|\   __  \|\  \    /  /|\  ___ \ |\   __  \                   
\ \  \___|\ \  \|\  \ \  \  /  / | \   __/|\ \  \|\  \            
 \ \  \    \ \   __  \ \  \/  / / \ \  \_|/_\ \   _  _\             
  \ \  \____\ \  \ \  \ \    / /   \ \  \_|\ \ \  \\  \|        
   \ \_______\ \__\ \__\ \__/ /     \ \_______\ \__\\ _\           
    \|_______|\|__|\|__|\|__|/       \|_______|\|__|\|__|                  
                                                                                  
                       ___  ________  ___      ___ ________                       
                      |\  \|\   __  \|\  \    /  /|\   __  \              
 ____________         \ \  \ \  \|\  \ \  \  /  / | \  \|\  \        
|\____________\     __ \ \  \ \   __  \ \  \/  / / \ \   __  \        
\|____________|    |\  \\_\  \ \  \ \  \ \    / /   \ \  \ \  \   
                   \ \________\ \__\ \__\ \__/ /     \ \__\ \__\     
                    \|________|\|__|\|__|\|__|/       \|__|\|__|            
                                                                                  
Usage: caver-java solidity|truffle ...
```
## Related projects 
**caver-js** for a javascript
## Build instructions
TBD
## Snapshot dependencies
TBD
## Thanks
- The [web3j](https://github.com/web3j/web3j) project for the inspiration.  🙂 