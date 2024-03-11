package br.com.nogueira.picpaydesafiobackend.transaction;

import br.com.nogueira.picpaydesafiobackend.authorization.AuthorizerService;
import br.com.nogueira.picpaydesafiobackend.notification.NotificationService;
import br.com.nogueira.picpaydesafiobackend.wallet.Wallet;
import br.com.nogueira.picpaydesafiobackend.wallet.WalletRepository;
import br.com.nogueira.picpaydesafiobackend.wallet.WalletType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactioRepository transactioRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;

    private final NotificationService notificationService;

    public TransactionService(TransactioRepository transactioRepository, WalletRepository walletRepository, AuthorizerService authorizerService, NotificationService notificationService) {
        this.transactioRepository = transactioRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        validate(transaction);
        var newTransaction = transactioRepository.save(transaction);
        var walletPayer = walletRepository.findById(transaction.payer()).get();
        var walletPayee = walletRepository.findById(transaction.payee()).get();
        walletRepository.save(walletPayer.debit(transaction.value()));
        walletRepository.save(walletPayee.credit(transaction.value()));
        authorizerService.authorize(transaction);
        notificationService.notify(transaction);
        return newTransaction;
    }

    /**
     * - the payer has a common wallet
     * - the payer has enough balance
     * - the payer is not the payee
     */
    private void validate(Transaction transaction) {
        walletRepository.findById(transaction.payee())
                .map(payee -> walletRepository.findById(transaction.payer())
                        .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
                        .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s - ".formatted(transaction.payer())))
                ).orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s - ".formatted(transaction.payer())));

    }

    private static boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue() &&
                payer.balance().compareTo(transaction.value()) >= 0 &&
                !payer.id().equals(transaction.payee());
    }

    public List<Transaction> getTransactions() {
        return transactioRepository.findAll();
    }
}
