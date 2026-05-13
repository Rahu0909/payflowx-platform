package com.payflowx.settlement.service;

public interface ReconciliationService {

    void reconcilePayments();

    void reconcileMerchantBalances();
}