package com.payflowx.settlement.entity;

import com.payflowx.settlement.enums.LedgerAccountType;
import com.payflowx.settlement.enums.LedgerEntryType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "ledger_entries",
        indexes = {
                @Index(
                        name = "idx_ledger_transaction",
                        columnList = "transaction_reference"
                ),
                @Index(
                        name = "idx_ledger_account",
                        columnList = "account_type"
                ), @Index(
                        name = "idx_ledger_merchant",
                        columnList = "merchant_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry
        extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(
            name = "transaction_reference",
            nullable = false,
            length = 100
    )
    private String transactionReference;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "account_type",
            nullable = false,
            length = 50
    )
    private LedgerAccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "entry_type",
            nullable = false,
            length = 20
    )
    private LedgerEntryType entryType;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "currency",
            nullable = false,
            length = 10
    )
    private String currency;

    @Column(
            name = "reference_id"
    )
    private UUID referenceId;

    @Column(
            name = "description",
            length = 500
    )
    private String description;
}