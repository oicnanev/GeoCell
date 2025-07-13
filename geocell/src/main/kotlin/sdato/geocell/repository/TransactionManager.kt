package sdato.geocell.repository

import sdato.geocell.repository.Transaction

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}