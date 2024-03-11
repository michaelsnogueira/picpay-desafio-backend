package br.com.nogueira.picpaydesafiobackend.transaction;

import org.springframework.data.repository.ListCrudRepository;

public interface TransactioRepository extends ListCrudRepository<Transaction, Long>{
}
