/*
 * Copyright 2019 The caver-java Authors
 *
 * Licensed under the Apache License, Version 2.0 (the “License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klaytn.caver.feature;

import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.request.CallObject;
import com.klaytn.caver.methods.request.KlayFilter;
import com.klaytn.caver.methods.request.KlayLogFilter;
import com.klaytn.caver.methods.response.Boolean;
import com.klaytn.caver.methods.response.*;
import com.klaytn.caver.scenario.Scenario;
import com.klaytn.caver.utils.ChainId;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Response;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.klaytn.caver.base.Accounts.BRANDON;
import static com.klaytn.caver.base.Accounts.LUMAN;
import static junit.framework.TestCase.*;

public class RpcTest {

    private Caver caver;
    private TestBlock testBlock;
    private String testTransactionHash;

    public static class TestBlock {
        int blockNumber;
        String blockHash;
        int transactionCount;

        public TestBlock(int blockNumber, String blockHash, int transactionCount) {
            this.blockNumber = blockNumber;
            this.blockHash = blockHash;
            this.transactionCount = transactionCount;
        }
    }

    @Before
    public void setUp() {
        this.caver = Caver.build(Caver.BAOBAB_URL);
        this.testBlock = new TestBlock(
                36523,
                "0x307b87383974dcf419436f0f30e1dd71945673282cda9b63e7de30361567a46e",
                1);
        this.testTransactionHash = "0x26ce172b055cc5adc26cc1350868c9fe4840729d4cc03b3f9f0a934eebdd0cec";
    }

    @Test
    public void testIsAccountCreated() throws Exception {
        Boolean response = caver.klay().isAccountCreated(
                LUMAN.getAddress(),
                DefaultBlockParameterName.LATEST
        ).send();
        assertTrue(response.getResult());
    }

    @Test
    public void testGetAccounts() throws Exception {
        Addresses response = caver.klay().getAccounts().send();
        assertNull(response.getError());
    }

    @Test
    public void getAccount() throws IOException {
        KlayAccount response = caver.klay().getAccount("0x1b24096b5a84d0f422faaa69f4de65d24329bd87", DefaultBlockParameterName.LATEST).send();
        KlayAccount.Account account = response.getResult();
        assertEquals(0x2, account.getAccType());
    }

    @Test
    public void getAccountKey() throws IOException {
        KlayAccountKey response = caver.klay().getAccountKey("0x9300da845f40c25336e3fbaec88a1842f3029a95", DefaultBlockParameterName.LATEST).send();
        KlayAccountKey.AccountKeyValue accountKey = response.getResult();
        assertEquals(0x05, accountKey.getKeyType());
    }

    @Test
    public void testGetBalance() throws Exception {
        Quantity response = caver.klay().getBalance(LUMAN.getAddress(), DefaultBlockParameterName.LATEST).send();
        String result = response.getResult();
        assertNotNull(result);
    }

    @Test
    public void testGetCode() throws Exception {
        Response<String> response = caver.klay().getCode("0x9bf734fcacf0151d81f5d895898bf3afeb1b6e0b", DefaultBlockParameterName.LATEST).send();
        String result = response.getResult();
        assertEquals("0x6080604052600436106053576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306661abd14605857806342cbb15c146080578063d14e62b81460a8575b600080fd5b348015606357600080fd5b50606a60d2565b6040518082815260200191505060405180910390f35b348015608b57600080fd5b50609260d8565b6040518082815260200191505060405180910390f35b34801560b357600080fd5b5060d06004803603810190808035906020019092919050505060e0565b005b60005481565b600043905090565b80600081905550505600a165627a7a7230582064856de85a2706463526593b08dd790054536042ef66d3204018e6790a2208d10029",
                result);
    }

    @Test
    public void testGetTransactionCount() throws Exception {
        Quantity response = caver.klay().getTransactionCount(
                LUMAN.getAddress(),
                DefaultBlockParameterName.LATEST).send();
        BigInteger result = response.getValue();
        assertNotNull(result);
    }

    @Test
    public void testIsContractAccount() throws Exception {
        Boolean response = caver.klay().isContractAccount(
                "0xd176364a6e4c0737efbee4c598835633de50f3f0", DefaultBlockParameterName.LATEST).send();
        java.lang.Boolean result = response.getResult();
        assertTrue(result);
    }

    @Test
    @Ignore
    public void testSign() throws Exception {
        // need a account and unlock
        caver.klay().unlockAccount(LUMAN.getAddress(), "password", BigInteger.valueOf(10)).send();

        Response<String> response = caver.klay().sign(LUMAN.getAddress(), "0xdeadbeaf").send();
        String result = response.getResult();
        assertEquals("0x8bb279f497d55a9068b9941f056d7b5e70bead57167d0e12aa3576a33766567f23135209a7a08992288cff75a4c60fb1f5c131e906dd0e714722b89a6964a1b51b",
                result);
    }

    @Test
    public void testGetBlockNumber() throws Exception {
        Quantity response = caver.klay().getBlockNumber().send();
        BigInteger result = response.getValue();
        assertNotNull(result);
    }

    @Test
    public void testGetBlockByNumber() throws Exception {
        KlayBlock response = caver.klay().getBlockByNumber(
                new DefaultBlockParameterNumber(testBlock.blockNumber),
                true).send();
        KlayBlock.Block result = response.getResult();
        TestCase.assertEquals(testBlock.blockHash, result.getTransactions().get(0).getBlockHash());
    }

    @Test
    public void testGetBlockByHash() throws Exception {
        KlayBlock response = caver.klay().getBlockByHash(testBlock.blockHash, true).send();
        KlayBlock.Block result = response.getResult();
        TestCase.assertEquals(testBlock.blockHash, result.getTransactions().get(0).getBlockHash());
    }

    @Test
    public void testGetBlockReceipts() throws Exception {
        BlockReceipts response = caver.klay().getBlockReceipts(testBlock.blockHash).send();
        List<KlayTransactionReceipt.TransactionReceipt> result = response.getResult();
        assertEquals(testBlock.blockHash, result.get(0).getBlockHash());
    }

    @Test
    public void testGetTransactionCountByNumber() throws Exception {
        Quantity response = caver.klay().getTransactionCountByNumber(
                new DefaultBlockParameterNumber(testBlock.blockNumber)).send();
        BigInteger result = response.getValue();
        assertEquals(testBlock.transactionCount, result.intValue());
    }

    @Test
    public void testGetTransactionCountByHash() throws Exception {
        Quantity response = caver.klay().getTransactionCountByHash(testBlock.blockHash).send();
        BigInteger result = response.getValue();
        assertEquals(testBlock.transactionCount, result.intValue());
    }

    @Test
    public void testGetBlockWithConsensusInfoByHash() throws Exception {
        KlayBlockWithConsensusInfo response = caver.klay().getBlockWithConsensusInfoByHash(testBlock.blockHash).send();
        KlayBlockWithConsensusInfo.Block result = response.getResult();
        assertEquals(testBlock.blockHash, result.getHash());
    }

    @Test
    public void testGetBlockWithConsensusInfoByNumber() throws Exception {
        KlayBlockWithConsensusInfo response = caver.klay().getBlockWithConsensusInfoByNumber(
                new DefaultBlockParameterNumber(testBlock.blockNumber)).send();
        KlayBlockWithConsensusInfo.Block result = response.getResult();
        assertEquals(testBlock.blockNumber, Numeric.toBigInt(result.getNumber()).intValue());
    }

    @Test
    public void testGetCommittee() throws IOException {
        Addresses response = caver.klay().getCommittee(DefaultBlockParameterName.LATEST).send();
        assertNull(response.getError());
    }

    @Test
    public void testGetCommitteeSize() throws IOException {
        Quantity response = caver.klay().getCommitteeSize(DefaultBlockParameterName.LATEST).send();
        assertNull(response.getError());
    }

    @Test
    public void testGetCouncil() throws IOException {
        Addresses response = caver.klay().getCouncil(DefaultBlockParameterName.LATEST).send();
        assertNull(response.getError());
    }

    @Test
    public void testGetCouncilSize() throws IOException {
        Quantity response = caver.klay().getCouncilSize(DefaultBlockParameterName.LATEST).send();
        assertNull(response.getError());
    }

    @Test
    public void testGetStorageAt() throws Exception {
        Response<String> response = caver.klay().getStorageAt(
                LUMAN.getAddress(),
                new DefaultBlockParameterNumber(0),
                DefaultBlockParameterName.LATEST).send();
        String result = response.getResult();
        assertNotNull(result);
    }

    @Test
    public void testGetWork() throws Exception {
        Response<List<String>> response = caver.klay().getWork().send();
        List<String> result = response.getResult();
        assertEquals(3, result.size());
        assertTrue(result.get(0).matches("0x\\w{64}$"));
        assertTrue(result.get(1).matches("0x\\w{64}$"));
        assertTrue(result.get(2).matches("0x\\w{64}$"));
    }

    @Test
    public void testIsMining() throws Exception {
        Boolean result = caver.klay().isMining().send();
        boolean isMining = result.getResult();
        assertTrue(isMining);
    }

    @Test
    public void testIsSyncing() throws Exception {
        KlaySyncing response = caver.klay().isSyncing().send();
        KlaySyncing.Result result = response.getResult();
        assertFalse(result.isSyncing());
    }

    @Test
    public void testCall() throws Exception {
        CallObject callObject = CallObject.createCallObject(
                LUMAN.getAddress(),
                "0xB18f35236a5296ccf2cc24EC67487E07C224b98E",
                new BigInteger("2dc6c0", 16),
                new BigInteger("5d21dba00", 16),
                new BigInteger("0", 16),
                "0x06661abd"  // count()
        );
        Response<String> response = caver.klay().call(callObject, DefaultBlockParameterName.LATEST).send();
        String result = response.getResult();
        assertEquals(27, Numeric.toBigInt(result).intValue());
    }

    @Test
    public void testEstimateGas() throws Exception {
        CallObject callObject = CallObject.createCallObject(
                "0x3f71029af4e252b25b9ab999f77182f0cd3bc085",
                "0x87ac99835e67168d4f9a40580f8f5c33550ba88b",
                new BigInteger("100000", 16),
                new BigInteger("5d21dba00", 16),
                new BigInteger("0", 16),
                "0x8ada066e"
        );
        Quantity response = caver.klay().estimateGas(callObject).send();
        String result = response.getResult();
        assertEquals("0x5318", result);
    }

    @Test
    @Ignore
    public void testEstimateComputationCost() throws Exception {
        CallObject callObject = CallObject.createCallObject(
                BRANDON.getAddress(),
                "0xa59796a9dab38fbdd8c4061c3c10493cd7c4b37e",
                new BigInteger("100000", 16),
                new BigInteger("5d21dba00", 16),
                new BigInteger("0", 16),
                "0x8ada066e"
        );
        Quantity response = caver.klay().estimateComputationCost(callObject, DefaultBlockParameterName.LATEST).send();
        String result = response.getResult();
        assertEquals("0x5318", result);
    }

    @Test
    public void testGetTransactionByBlockHashAndIndex() throws Exception {
        KlayTransaction response = caver.klay().getTransactionByBlockHashAndIndex(
                testBlock.blockHash,
                new DefaultBlockParameterNumber(0)
        ).send();
        Optional<KlayTransaction.Transaction> result = response.getTransaction();
        assertEquals(testBlock.blockHash, result.get().getBlockHash());
    }

    @Test
    public void testGetTransactionByBlockNumberAndIndex() throws Exception {
        KlayTransaction response = caver.klay().getTransactionByBlockNumberAndIndex(
                new DefaultBlockParameterNumber(testBlock.blockNumber),
                new DefaultBlockParameterNumber(0)
        ).send();
        Optional<KlayTransaction.Transaction> result = response.getTransaction();
        assertEquals(testBlock.blockHash, result.get().getBlockHash());
    }

    @Test
    public void testGetTransactionByHash() throws Exception {
        KlayTransaction response = caver.klay().getTransactionByHash(testTransactionHash).send();
        Optional<KlayTransaction.Transaction> result = response.getTransaction();
        assertEquals(testTransactionHash, result.get().getHash());
    }

    @Test
    public void testGetTransactionBySenderTxHash() throws Exception {
        KlayTransaction response = caver.klay().getTransactionBySenderTxHash(testTransactionHash).send();
        Optional<KlayTransaction.Transaction> result = response.getTransaction();
        assertEquals(testTransactionHash, result.get().getHash());
    }

    @Test
    public void testGetTransactionReceipt() throws Exception {
        KlayTransactionReceipt response = caver.klay().getTransactionReceipt(testTransactionHash).send();
        Optional<KlayTransactionReceipt.TransactionReceipt> result = response.getTransactionReceipt();
        assertEquals(testTransactionHash, result.get().getTransactionHash());
    }

    @Test
    public void testGetTransactionReceiptBySenderTxHash() throws Exception {
        KlayTransactionReceipt response = caver.klay().getTransactionReceiptBySenderTxHash(testTransactionHash).send();
        Optional<KlayTransactionReceipt.TransactionReceipt> result = response.getTransactionReceipt();
        assertEquals(testTransactionHash, result.get().getTransactionHash());
    }

    @Ignore
    @Test
    public void testSendSignedTransaction() {
        // SendKlayIT.java
    }

    @Ignore
    @Test
    public void testSendTransaction() {
        // SendKlayIT.java
    }

    @Ignore
    @Test
    public void testSignTransaction() {
        // SendKlayIT.java

    }

    @Test
    public void testGetChainId() throws Exception {
        Quantity response = caver.klay().getChainID().send();
        BigInteger result = response.getValue();
        assertEquals(BigInteger.valueOf(ChainId.BAOBAB_TESTNET), result);
    }

    @Test
    public void testGetClientVersion() throws IOException {
        Bytes response = caver.klay().getClientVersion().send();
        assertNull(response.getError());
    }

    @Test
    public void testGetGasPrice() throws Exception {
        Quantity response = caver.klay().getGasPrice().send();
        BigInteger result = response.getValue();
        assertEquals(new BigInteger("5d21dba00", 16), result); // 25,000,000,000 peb = 25 Gpeb
    }

    @Test
    public void testGetGasPriceAt() throws IOException {
        Quantity response = caver.klay().getGasPriceAt(null).send();
        BigInteger result = response.getValue();
        assertEquals(new BigInteger("5d21dba00", 16), result); // 25,000,000,000 peb = 25 Gpeb
    }

    @Test
    public void testIsParallelDbWrite() throws Exception {
        Boolean response = caver.klay().isParallelDBWrite().send();
        java.lang.Boolean result = response.getResult();
        assertTrue(result);  // It is enabled by default
    }

    @Test
    public void testIsSenderTxHashIndexingEnabled() throws Exception {
        Boolean response = caver.klay().isSenderTxHashIndexingEnabled().send();
        java.lang.Boolean result = response.getResult();
        assertFalse(result);  // It is disabled by default
    }

    @Test
    public void testGetProtocolVersion() throws Exception {
        String result = caver.klay().getProtocolVersion().send().getResult();
        assertEquals("0x40", result);
    }

    @Ignore
    @Test
    public void testGetRewardbase() throws Exception {
        Bytes20 response = caver.klay().getRewardbase().send();
        // Result - If requested from non-CN nodes
        assertEquals("rewardbase must be explicitly specified", response.getError().getMessage());
    }

    @Test
    public void testIsWriteThroughCaching() throws Exception {
        Boolean response = caver.klay().isWriteThroughCaching().send();
        java.lang.Boolean result = response.getResult();
        assertFalse(result);  // It is false by default.
    }

    @Test
    @Ignore
    public void testGetFilterChanges() throws Exception {
        KlayLogs response = caver.klay().getFilterChanges(
                new BigInteger("d5b93cf592b2050aee314767a02976c5", 16)).send();
        List<KlayLogs.LogResult> result = response.getResult();
        assertTrue("need test data", false);
    }

    @Test
    @Ignore
    public void testGetFilterLogs() throws Exception {
        KlayLogs response = caver.klay().getFilterLogs(
                new BigInteger("d5b93cf592b2050aee314767a02976c5", 16)).send();
        List<KlayLogs.LogResult> result = response.getResult();
        assertTrue("need test data", false);
    }

    @Test
    @Ignore
    public void testGetLogs() throws Exception {
        KlayLogFilter filter = new KlayLogFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                LUMAN.getAddress(),
                "0xe2649fe9fbaa75601408fc54200e3f9b2128e8fec7cea96c9a65b9caf905c9e3");
        KlayLogs response = caver.klay().getLogs(filter).send();
        List<KlayLogs.LogResult> result = response.getResult();
        assertTrue("need test data", false);
    }

    @Test
    public void testNewBlockFilter() throws Exception {
        Response<String> response = caver.klay().newBlockFilter().send();
        String result = response.getResult();
        assertTrue(result.matches("0x\\w{32}$"));
    }

    @Test
    public void testNewFilter() throws Exception {
        KlayFilter filter = new KlayFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                LUMAN.getAddress());
        filter.addSingleTopic("0xd596fdad182d29130ce218f4c1590c4b5ede105bee36690727baa6592bd2bfc8");
        Quantity response = caver.klay().newFilter(filter).send();
        String result = response.getResult();
        assertTrue(result.matches("0x\\w{32}$"));
    }

    @Test
    public void testNewPendingTransactionFilter() throws Exception {
        Response<String> response = caver.klay().newPendingTransactionFilter().send();
        String result = response.getResult();
        assertTrue(result.matches("0x\\w{32}$"));
    }

    @Test
    @Ignore
    public void testUninstallFilter() throws Exception {
        Boolean response = caver.klay().uninstallFilter(BigInteger.ZERO).send();
        java.lang.Boolean result = response.getResult();
        assertTrue("need test data", false);
    }

    @Test
    public void testGetSha3() throws IOException {
        Bytes response = caver.klay().getSha3("0x123f").send();
        String result = response.getResult();
        assertEquals("0x7fab6b214381d6479bf140c3c8967efb9babe535025500d5b1dc2d549984b90b", result);
    }

    @Test
    public void testGetId() throws Exception {
        Bytes netVersion = caver.net().getNetworkId().send();
        assertEquals(netVersion.getResult(), String.valueOf(Scenario.BAOBAB_CHAIN_ID));
    }

    @Test
    public void testIsListeninng() throws Exception {
        Boolean netListening = caver.net().isListening().send();
        assertTrue(netListening.getResult());
    }

    @Test
    public void testGetPeerCount() throws Exception {
        Quantity peerCount = caver.net().getPeerCount().send();
        assertTrue(peerCount.getValue().intValue() >= 0);
    }

    @Test
    public void testGetPeerCountByType() throws Exception {
        KlayPeerCount klayPeerCount = caver.net().getPeerCountByType().send();
        KlayPeerCount.PeerCount peerCount = klayPeerCount.getResult();
        assertTrue(peerCount.getTotal().intValue() >= 0);
    }
}
