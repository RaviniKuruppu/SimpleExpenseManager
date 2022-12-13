package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DataBase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccount;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransaction;

public class PersistentExpenseManager extends ExpenseManager{
    private DataBase myDataBase;
    public PersistentExpenseManager( Context context){
        myDataBase =new DataBase(context);
        setup();
    }
    @Override
    public void setup() {
        TransactionDAO persistentTransaction = new PersistentTransaction(this.myDataBase);
        setTransactionsDAO(persistentTransaction);

        AccountDAO persistentAccount = new PersistentAccount(this.myDataBase);
        setAccountsDAO(persistentAccount);

    }
}
