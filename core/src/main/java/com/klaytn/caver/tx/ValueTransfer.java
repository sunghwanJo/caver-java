/*
 * Modifications copyright 2019 The caver-java Authors
 * Copyright 2016 Conor Svensson
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
 *
 * This file is derived from web3j/core/src/main/java/org/web3j/tx/Transfer.java (2019/06/13).
 * Modified and improved for the caver-java development.
 */

package com.klaytn.caver.tx;

import com.klaytn.caver.Caver;
import com.klaytn.caver.crpyto.KlayCredentials;
import com.klaytn.caver.methods.response.KlayTransactionReceipt;
import com.klaytn.caver.tx.manager.ErrorHandler;
import com.klaytn.caver.tx.manager.TransactionManager;
import com.klaytn.caver.utils.Convert;
import com.klaytn.caver.tx.model.ValueTransferTransaction;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ValueTransfer extends ManagedTransaction {

    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(21000);

    public ValueTransfer(Caver caver, TransactionManager transactionManager) {
        super(caver, transactionManager);
    }

    private KlayTransactionReceipt.TransactionReceipt send(
            String fromAddress, String toAddress, BigDecimal value, Convert.Unit unit,
            BigInteger gasLimit) {

        BigDecimal klayValue = Convert.toPeb(value, unit);
        if (!Numeric.isIntegerValue(klayValue)) {
            throw new UnsupportedOperationException(
                    "Non decimal Peb value provided: " + value + " " + unit.toString()
                            + " = " + klayValue + " Peb");
        }

        ValueTransferTransaction transaction = ValueTransferTransaction.create(
                fromAddress, toAddress, klayValue.toBigIntegerExact(), gasLimit);

        return send(transaction);
    }

    public static RemoteCall<KlayTransactionReceipt.TransactionReceipt> sendFunds(
            Caver caver, KlayCredentials credentials, String toAddress, BigDecimal value, Convert.Unit unit, BigInteger gasLimit) {

        return ValueTransfer.sendFunds(caver, credentials, toAddress, value, unit, gasLimit, null);
    }

    public static RemoteCall<KlayTransactionReceipt.TransactionReceipt> sendFunds(
            Caver caver, KlayCredentials credentials, String toAddress, BigDecimal value, Convert.Unit unit, BigInteger gasLimit, ErrorHandler errorHandler) {

        TransactionManager transactionManager = new TransactionManager.Builder(caver, credentials)
                .setErrorHandler(errorHandler)
                .build();

        return new RemoteCall<>(() ->
                new ValueTransfer(caver, transactionManager).send(credentials.getAddress(), toAddress, value, unit, gasLimit));
    }

    public static RemoteCall<KlayTransactionReceipt.TransactionReceipt> sendFunds(
            Caver caver, KlayCredentials credentials, ValueTransferTransaction transaction) {

        return ValueTransfer.sendFunds(caver, credentials, transaction, null);
    }

    public static RemoteCall<KlayTransactionReceipt.TransactionReceipt> sendFunds(
            Caver caver, KlayCredentials credentials, ValueTransferTransaction transaction, ErrorHandler errorHandler) {

        TransactionManager transactionManager = new TransactionManager.Builder(caver, credentials)
                .setErrorHandler(errorHandler)
                .build();

        return new RemoteCall<>(() ->
                new ValueTransfer(caver, transactionManager).send(transaction));
    }

    public RemoteCall<KlayTransactionReceipt.TransactionReceipt> sendFunds(
            String fromAddress, String toAddress, BigDecimal value, Convert.Unit unit, BigInteger gasLimit) {
        return new RemoteCall<>(() -> send(fromAddress, toAddress, value, unit, gasLimit));
    }

    public RemoteCall<KlayTransactionReceipt.TransactionReceipt> sendFunds(ValueTransferTransaction transaction) {
        return new RemoteCall<>(() -> send(transaction));
    }
}
